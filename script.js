let materials = [
  {id:101, title:"Stack Notes", subject:"Data Structures", topic:"Stack", type:"Notes", semester:3, keywords:"stack, data, structures", resource:"DS/StackNotes.pdf"},
  {id:102, title:"Linked List Practical", subject:"Data Structures", topic:"Linked List", type:"Practical", semester:3, keywords:"linked list, practical, node", resource:"DS/LinkedList.java"},
  {id:103, title:"SQL Queries", subject:"DBMS", topic:"SQL", type:"Notes", semester:3, keywords:"sql, database, queries", resource:"DBMS/SQL.pdf"},
  {id:104, title:"Previous Year Questions", subject:"Operating System", topic:"Processes", type:"PYQ", semester:4, keywords:"process, os, pyq", resource:"OS/PYQ.pdf"},
  {id:105, title:"Python Basics", subject:"Python", topic:"Variables", type:"PPT", semester:2, keywords:"python, variables, basics", resource:"Python/Basics.pptx"}
];

let searchHistory = [];

function addHistory(text) {
  searchHistory.push(text);
}

function displayAll(list = materials, title = "All Materials") {
  document.getElementById("outputTitle").textContent = title;
  if (!list.length) {
    document.getElementById("output").innerHTML = "<p>No materials found.</p>";
    return;
  }
  let rows = list.map(m => `
    <tr>
      <td>${m.id}</td><td>${m.title}</td><td>${m.subject}</td>
      <td>${m.topic}</td><td>${m.type}</td><td>${m.semester}</td>
      <td>${m.keywords}</td><td>${m.resource}</td>
    </tr>`).join("");
  document.getElementById("output").innerHTML = `
    <table>
      <thead><tr><th>ID</th><th>Title</th><th>Subject</th><th>Topic</th>
      <th>Type</th><th>Semester</th><th>Keywords</th><th>Resource</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
}

function addMaterial() {
  let id = Number(prompt("Enter Material ID:"));
  if (!id || materials.some(m => m.id === id)) return alert("Invalid or duplicate ID.");
  let title = prompt("Enter Title:") || "";
  let subject = prompt("Enter Subject:") || "";
  let topic = prompt("Enter Topic:") || "";
  let type = prompt("Enter Type (Notes/Practical/PPT/PYQ):") || "";
  let semester = Number(prompt("Enter Semester:"));
  let keywords = prompt("Enter Keywords:") || "";
  let resource = prompt("Enter Resource Path:") || "";
  materials.push({id, title, subject, topic, type, semester, keywords, resource});
  alert("Material added successfully.");
  displayAll();
}

function searchById() {
  let id = Number(prompt("Enter Material ID:"));
  let result = materials.filter(m => m.id === id);
  addHistory("ID: " + id);
  displayAll(result, "Search Result by ID");
}

function searchBySubject() {
  let subject = prompt("Enter Subject:");
  if (!subject) return;
  let result = materials.filter(m => m.subject.toLowerCase() === subject.toLowerCase());
  addHistory("Subject: " + subject);
  displayAll(result, "Search Result by Subject");
}

function searchMaterial() {
  let keyword = prompt("Enter keyword/prefix:");
  if (!keyword) return;
  let q = keyword.toLowerCase();
  let result = materials.filter(m =>
    (m.title + " " + m.subject + " " + m.topic + " " + m.keywords).toLowerCase().includes(q)
  );
  addHistory("Keyword: " + keyword);
  let suggestions = [...new Set(materials.flatMap(m => [m.title, m.subject, m.topic].filter(x => x.toLowerCase().startsWith(q))))];
  displayAll(result, "Search / Autocomplete Result");
  if (suggestions.length) {
    document.getElementById("output").insertAdjacentHTML("afterbegin",
      `<div class="card"><strong>Autocomplete Suggestions:</strong><div class="suggestion">${suggestions.join(" • ")}</div></div>`);
  }
}

function sortByTitle() {
  let result = [...materials].sort((a,b) => a.title.localeCompare(b.title));
  displayAll(result, "Sorted by Title");
}

function sortBySubject() {
  let result = [...materials].sort((a,b) => a.subject.localeCompare(b.subject));
  displayAll(result, "Sorted by Subject");
}

function deleteMaterial() {
  let id = Number(prompt("Enter Material ID to delete:"));
  let oldLength = materials.length;
  materials = materials.filter(m => m.id !== id);
  alert(materials.length < oldLength ? "Material deleted successfully." : "Material ID not found.");
  displayAll();
}

function showHistory() {
  document.getElementById("outputTitle").textContent = "Search History";
  document.getElementById("output").innerHTML = searchHistory.length
    ? `<div class="card">${searchHistory.slice().reverse().map((x,i) => `<div>${i+1}. ${x}</div>`).join("")}</div>`
    : "<p>No search history yet.</p>";
}

function showStatistics() {
  let subjects = new Set(materials.map(m => m.subject)).size;
  let types = new Set(materials.map(m => m.type)).size;
  document.getElementById("outputTitle").textContent = "Statistics";
  document.getElementById("output").innerHTML = `
    <div class="card"><strong>Total Materials:</strong> ${materials.length}</div>
    <div class="card"><strong>Total Subjects:</strong> ${subjects}</div>
    <div class="card"><strong>Total Material Types:</strong> ${types}</div>`;
}

displayAll();
