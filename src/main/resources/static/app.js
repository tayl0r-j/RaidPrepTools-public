const API = {
  teams: "/api/raid-teams",
  members: "/api/raid-team-members"
};

let teams = [];
let selectedTeam = null;
let selectedMember = null;
let draggedMember = null;

// ---------- Raid Size Templates ----------
const RAID_TEMPLATES = {
  10: 2,  // 2 groups of 5
  25: 5,  // 5 groups of 5
  40: 8,  // 8 groups of 5
};

// ---------- Class/Spec Mapping ----------
const CLASS_TO_SPECS = {
  WARRIOR: ["ARMS", "FURY", "PROTECTION"],
  PRIEST: ["HOLY", "DISCIPLINE", "SHADOW"],
  DRUID: ["RESTORATION_DRUID", "BALANCE", "FERAL"],
  MAGE: ["ARCANE", "FIRE", "FROST"],
  ROGUE: ["ASSASSINATION", "COMBAT", "SUBTLETY"],
  HUNTER: ["BEAST_MASTERY", "MARKSMANSHIP", "SURVIVAL"],
  WARLOCK: ["AFFLICTION", "DEMONOLOGY", "DESTRUCTION"],
  PALADIN: ["HOLY_PALADIN", "PROTECTION_PALADIN", "RETRIBUTION_PALADIN"],
  SHAMAN: ["ELEMENTAL", "ENHANCEMENT", "RESTORATION_SHAMAN"],
};

const SPEC_TO_ROLE = {
  // Warrior
  ARMS: "MELEE",
  FURY: "MELEE",
  PROTECTION: "TANK",

  // Priest
  HOLY: "HEALER",
  DISCIPLINE: "HEALER",
  SHADOW: "RANGED",

  // Druid
  RESTORATION_DRUID: "HEALER",
  BALANCE: "RANGED",
  FERAL: "MELEE", // or "TANK" if you prefer that use

  // Mage
  ARCANE: "RANGED",
  FIRE: "RANGED",
  FROST: "RANGED",

  // Rogue
  ASSASSINATION: "MELEE",
  COMBAT: "MELEE",
  SUBTLETY: "MELEE",

  // Hunter
  BEAST_MASTERY: "RANGED",
  MARKSMANSHIP: "RANGED",
  SURVIVAL: "RANGED",

  // Warlock
  AFFLICTION: "RANGED",
  DEMONOLOGY: "RANGED",
  DESTRUCTION: "RANGED",

  // Paladin
  HOLY_PALADIN: "HEALER",
  PROTECTION_PALADIN: "TANK",
  RETRIBUTION_PALADIN: "MELEE",

  // Shaman
  ELEMENTAL: "RANGED",
  ENHANCEMENT: "MELEE",
  RESTORATION_SHAMAN: "HEALER",
};

function inferRoleFromSpec(spec) {
  return SPEC_TO_ROLE[spec] || "";
}

function specLabel(enumValue) {
  // "BEAST_MASTERY" -> "Beast Mastery"
  return enumValue
    .toLowerCase()
    .split("_")
    .map(w => w.charAt(0).toUpperCase() + w.slice(1))
    .join(" ");
}

function resetSpecDropdown() {
  const specSelect = $("playerSpec");
  specSelect.innerHTML = '<option value="">Spec</option>';
  specSelect.disabled = true;
}

function populateSpecDropdown(classValue) {
  const specSelect = $("playerSpec");

  if (!classValue || !CLASS_TO_SPECS[classValue]) {
    resetSpecDropdown();
    return;
  }

  const specs = CLASS_TO_SPECS[classValue];

  specSelect.disabled = false;
  specSelect.innerHTML = '<option value="">Spec</option>';

  specs.forEach(spec => {
    const opt = document.createElement("option");
    opt.value = spec;              // enum value to send to backend
    opt.textContent = specLabel(spec); // nice label for UI
    specSelect.appendChild(opt);
  });
}

function classIconUrl(playerClass) {
  if (!playerClass) return null;
  playerClass = playerClass.toUpperCase();
  // Assumes: src/main/resources/static/icons/class/WARRIOR.png etc.
  return `/icons/class/${playerClass}.png`;
}

// ---------- Helpers ----------
const $ = (id) => document.getElementById(id);
const msg = (el, text, type = "muted") => {
  el.className = type;
  el.textContent = text;
};

async function apiFetch(url, options = {}) {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options
  });
  if (!res.ok) {
    const body = await res.text();
    throw new Error(body || res.statusText);
  }
  return res.json();
}

// ---------- Render ----------
function renderTeams() {
  const list = $("teamsList");
  list.innerHTML = "";

  teams.forEach(t => {
    const div = document.createElement("div");
    div.className = "team-item" + (selectedTeam?.raidTeamId === t.raidTeamId ? " active" : "");
    div.textContent = `${t.teamName} (#${t.raidTeamId})`;
    div.onclick = () => selectTeam(t.raidTeamId);
    list.appendChild(div);
  });

  msg($("teamMsg"), `${teams.length} team(s) loaded`);
}

function renderMembers() {
  const list = $("membersList");
  list.innerHTML = "";

  if (!selectedTeam) return;

  // --- Slotted members ---
  const benchMembers = selectedTeam.members.filter(
    m => m.groupIndex == null && m.slotIndex == null
  );

  benchMembers.forEach(m => {
    const div = document.createElement("div");
    div.className =
      "member class-" + m.playerClass +
      (selectedMember?.raiderId === m.raiderId ? " active" : "");

    // Location: if somehow partially slotted, still show G/S; otherwise "Bench"
    const locationText =
      m.groupIndex != null && m.slotIndex != null
        ? `G${m.groupIndex} S${m.slotIndex}`
        : "Bench";

    const cleanStatus = m.status?.replace(/_/g, " ") ?? "";

    div.innerHTML = `
        <div><strong class="name">${m.playerName}</strong> (#${m.raiderId})</div>
        <div class="muted">
          ${m.playerClass} • ${m.playerSpec}
          <span class="tag tag-role-${m.role}">${m.role}</span>
          <span class="tag tag-status-${m.status}">${cleanStatus}</span>
        </div>
        <div class="muted">${locationText}</div>
      `;

    // Click to select
    div.onclick = () => {
      selectedMember = m;
      $("clearSlotBtn").disabled = false;
      renderMembers();
      renderGrid();
    };

    // Enable drag-and-drop from bench
    div.draggable = true;
    div.ondragstart = (e) => handleMemberDragStart(e, m);
    div.ondragend = handleMemberDragEnd;

    list.appendChild(div);
  });
}

function renderGrid() {
  const container = $("gridContainer");
  container.innerHTML = "";

  if (!selectedTeam) return;

  // --- Raid size & group count ---
  const raidSizeEl = $("raidSize");
  const raidSize = raidSizeEl ? Number(raidSizeEl.value) : 40;
  const groupCount = RAID_TEMPLATES[raidSize] || 8;

  // --- Layout columns ---
  let cols;
  if (raidSize === 40) cols = 4;        // 8 groups → 4x2
  else if (raidSize === 25) cols = 5;   // 5 groups in 1 row
  else cols = 2;                        // 10-man → 2 groups

  // Dynamic min width (tight)
  let minWidth;
  if (raidSize === 40) {
    minWidth = 115;
  } else if (raidSize === 25) {
    minWidth = 130;
  } else {
    minWidth = 150;
  }

  // Apply layout
  container.style.display = "grid";
  container.style.gridTemplateColumns =
    `repeat(${cols}, minmax(${minWidth}px, max-content))`;  // ⬅ key change
  container.style.gap = "4px 4px";

  // Update title
  const titleEl = $("raidGridTitle");
  if (titleEl) titleEl.textContent = `Raid Grid`;

  // --- Build groups ---
  for (let g = 1; g <= groupCount; g++) {
    const groupCol = document.createElement("div");
    groupCol.className = "group-column";

    const header = document.createElement("div");
    header.className = "group-header";
    header.innerHTML = `
      <span>Group ${g}</span>
      
    `;
    groupCol.appendChild(header);

    const grid = document.createElement("div");
    grid.className = "grid";
    groupCol.appendChild(grid);

    for (let s = 1; s <= 5; s++) {
      const slot = document.createElement("div");

      const occupant = selectedTeam.members.find(
        (m) => m.groupIndex === g && m.slotIndex === s
      );

      slot.className = "slot" + (occupant ? ` filled class-${occupant.playerClass}` : "");

      if (occupant) {
        const iconUrl = classIconUrl(occupant.playerClass);
        slot.innerHTML = `
          <div class="raid-slot-inner">
            <div class="raid-slot-bg">
              <img src="${iconUrl}" class="raid-slot-bg-icon" />
            </div>
            <div class="raid-slot-fade"></div>
            <div class="raid-slot-footer">
              <span class="name">${occupant.playerName}</span>
            </div>
          </div>
        `;

        // make occupied slots draggable
        slot.draggable = true;
        slot.ondragstart = (e) => handleMemberDragStart(e, occupant);
        slot.ondragend = handleMemberDragEnd;

      } else {
        slot.innerHTML = `<small>EmptyG${g} S${s}</small>`;
      }

      slot.onclick = () => handleSlotClick(g, s, occupant);
      slot.ondragover = handleSlotDragOver;
      slot.ondragenter = (e) => handleSlotDragEnter(e, slot);
      slot.ondragleave = (e) => handleSlotDragLeave(e, slot);
      slot.ondrop = (e) => handleSlotDrop(e, g, s, occupant);

      grid.appendChild(slot);
    }

    container.appendChild(groupCol);
  }
}

// ---------- Actions ----------
async function loadTeams() {
  teams = await apiFetch(API.teams);
  renderTeams();

  // Don't override selectedTeam here.
  updateSelectedTeamUI();
}

async function saveTeamLayout(team) {
  await fetch(`/api/raid-teams/${team.raidTeamId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(team)
  });
}

async function selectTeam(id) {
  localStorage.setItem("selectedTeamId", id);

  selectedTeam = await apiFetch(`/api/raid-teams/${id}`);

  selectedMember = null;
  $("clearSlotBtn").disabled = true;

  updateSelectedTeamUI();
  renderTeams();
  renderMembers();
  renderGrid();
}

function updateSelectedTeamUI() {
  $("addMemberBtn").disabled = !selectedTeam;
  $("selectedTeamTitle").textContent =
    selectedTeam ? `${selectedTeam.teamName} (#${selectedTeam.raidTeamId})` : "Select a team";
}

async function createTeam() {
  const name = $("newTeamName").value.trim();
  if (!name) return;

  await apiFetch(API.teams, {
    method: "POST",
    body: JSON.stringify({ teamName: name })
  });

  $("newTeamName").value = "";
  await loadTeams();
}

async function addMember() {
  if (!selectedTeam) return;

  const playerName = $("playerName").value.trim();
  const playerClass = $("playerClass").value;
  const playerSpec = $("playerSpec").value.trim();
  const role = $("role").value;

  if (!playerName || !playerClass || !playerSpec || !role) {
    msg($("actionMsg"), "Fill name, class, spec, and role.", "error");
    return;
  }

  // Create payload
  const payload = {
    playerName,
    playerClass,
    playerSpec,
    role,
    status: "SIGNED_UP",
    team: { raidTeamId: selectedTeam.raidTeamId }
  };

  // Send to backend
  await apiFetch(API.members, {
    method: "POST",
    body: JSON.stringify(payload)
  });

  $("playerName").value = "";
  $("playerClass").value = "";
  $("playerSpec").value = "";
  $("role").value = "";

  msg($("actionMsg"), "Member added.", "success");
  await loadTeams();
  await selectTeam(selectedTeam.raidTeamId);
}

async function handleSlotClick(groupIndex, slotIndex, occupant) {
  if (!selectedMember) {
    msg($("actionMsg"), "Click a member first, then a slot.", "error");
    return;
  }

  // Clicking the slot that already contains this member: do nothing
  if (occupant && occupant.raiderId === selectedMember.raiderId) {
    return;
  }

  await moveMemberToSlot(selectedMember, groupIndex, slotIndex, occupant);
}

async function clearSelectedSlot() {
  if (!selectedMember) return;

  await apiFetch(`${API.members}/${selectedMember.raiderId}/slot`, {
    method: "PUT",
    body: JSON.stringify({ groupIndex: null, slotIndex: null })
  });

  msg($("actionMsg"), `Cleared slot for ${selectedMember.playerName}.`, "success");

  await loadTeams();
  await selectTeam(selectedTeam.raidTeamId);
}

function handleMemberDragStart(e, member) {
  draggedMember = member;
  // Optional: also store ID in dataTransfer for robustness
  e.dataTransfer.setData("text/plain", String(member.raiderId));
  e.dataTransfer.effectAllowed = "move";
}

function handleMemberDragEnd() {
  draggedMember = null;
  // Clear any highlight leftovers
  document.querySelectorAll(".slot.drop-target").forEach(el => {
    el.classList.remove("drop-target");
  });
}

// Slot drag-and-drop handlers
function handleSlotDragOver(e) {
  // Allow drop
  e.preventDefault();

  if (e.dataTransfer) {
    e.dataTransfer.dropEffect = "move";
  }
}


function handleSlotDragEnter(e, slotEl) {
  e.preventDefault();
  slotEl.classList.add("drop-target");
}

function handleSlotDragLeave(e, slotEl) {
  slotEl.classList.remove("drop-target");
}

async function handleSlotDrop(e, groupIndex, slotIndex, occupant) {
  e.preventDefault();

  const slotEl = e.currentTarget;
  slotEl.classList.remove("drop-target");

  if (!draggedMember) {
    msg($("actionMsg"), "No member selected for drag.", "error");
    return;
  }

  // Dropping onto the slot that already contains this member: no-op
  if (occupant && occupant.raiderId === draggedMember.raiderId) {
    draggedMember = null;
    return;
  }

  await moveMemberToSlot(draggedMember, groupIndex, slotIndex, occupant);
  draggedMember = null;
}

async function moveMemberToSlot(member, groupIndex, slotIndex, occupant) {
  if (!member || !selectedTeam) return;

  const updates = [];

  // 1) Place the dragged member into the target slot
  updates.push(
    apiFetch(`${API.members}/${member.raiderId}/slot`, {
      method: "PUT",
      body: JSON.stringify({ groupIndex, slotIndex })
    })
  );

  // 2) If someone was already in the slot, bump them back to bench
  if (occupant && occupant.raiderId !== member.raiderId) {
    updates.push(
      apiFetch(`${API.members}/${occupant.raiderId}/slot`, {
        method: "PUT",
        body: JSON.stringify({ groupIndex: null, slotIndex: null })
      })
    );
  }

  // Execute both updates in parallel
  await Promise.all(updates);

  msg(
    $("actionMsg"),
    `Placed ${member.playerName} in G${groupIndex} S${slotIndex}.`,
    "success"
  );

  // update UI only after all updates complete
  await selectTeam(selectedTeam.raidTeamId);
}


// ---------- Wire up ----------
$("createTeamBtn").onclick = createTeam;
$("addMemberBtn").onclick = addMember;
$("clearSlotBtn").onclick = clearSelectedSlot;

// wire class/spec dropdown
$("playerClass").addEventListener("change", (e) => {
  const selectedClass = e.target.value;
  populateSpecDropdown(selectedClass);
});

// auto-fill role from spec
$("playerSpec").addEventListener("change", () => {
  const spec = $("playerSpec").value;
  const role = inferRoleFromSpec(spec);
  if (role) {
    $("role").value = role;
  }
});

async function init() {
  await loadTeams();    // however you already load your list of teams

  const lastId = localStorage.getItem("selectedTeamId");
  if (lastId) {
    await selectTeam(parseInt(lastId));
  }
}

// Initialize spec dropdown state
resetSpecDropdown();

// Allow dropping onto the Members list to send a raider back to bench
const membersListEl = $("membersList");

if (membersListEl) {
  membersListEl.ondragover = (e) => {
    e.preventDefault();
    if (e.dataTransfer) {
      e.dataTransfer.dropEffect = "move";
    }
  };

  membersListEl.ondrop = async (e) => {
    e.preventDefault();

    if (!draggedMember) return;

    await apiFetch(`${API.members}/${draggedMember.raiderId}/slot`, {
      method: "PUT",
      body: JSON.stringify({ groupIndex: null, slotIndex: null })
    });

    msg(
      $("actionMsg"),
      `Moved ${draggedMember.playerName} to bench.`,
      "success"
    );

    draggedMember = null;

    await loadTeams();
    await selectTeam(selectedTeam.raidTeamId);
  };
}

// Start the app
init();

// wire raid size change
const raidSizeEl = $("raidSize");
if (raidSizeEl) {
  raidSizeEl.addEventListener("change", () => {
    renderGrid();
  });
}

