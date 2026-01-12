document.addEventListener("DOMContentLoaded", () => {
  const usersTable = document.getElementById("usersTable");
  const addUserForm = document.getElementById("addUserForm");

  // Fetch and display users
  const fetchUsers = async () => {
    try {
      const response = await axios.get("/admin/users");
      usersTable.innerHTML = response.data
        .map(
          (user) => `
          <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.role}</td>
            <td>
              <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Verwijderen</button>
            </td>
          </tr>
        `,
        )
        .join("");
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  // Add a new user
  addUserForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const role = document.getElementById("role").value;

    try {
      await axios.post("/admin/users", { username, role });
      addUserForm.reset();
      fetchUsers();
    } catch (error) {
      console.error("Error adding user:", error);
    }
  });

  // Delete a user
  window.deleteUser = async (id) => {
    if (confirm("Weet je zeker dat je deze gebruiker wilt verwijderen?")) {
      try {
        await axios.delete(`/admin/users/${id}`);
        fetchUsers();
      } catch (error) {
        console.error("Error deleting user:", error);
      }
    }
  };

  // Initial fetch
  fetchUsers();
});
