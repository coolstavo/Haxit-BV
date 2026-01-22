// Muzikant Profile JavaScript
// Handles jam creation, map interaction, and profile functionality

const currentMuzikantUserId = document.querySelector('[th\\:href*="/profile/"]')
  ? parseInt(
      document.querySelector("meta[data-user-id]")?.content ||
        window.location.pathname.split("/").pop(),
    )
  : null;

let jamMap;
let jamMarker;

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", function () {
  // Initialize jam map if the container exists
  if (document.getElementById("jamMap")) {
    setTimeout(() => initializeJamMap(), 100);
  }

  // Form submission
  const addJamForm = document.getElementById("addJamForm");
  if (addJamForm) {
    addJamForm.addEventListener("submit", handleJamFormSubmit);
  }

  // Delete confirmation on jams
  setupDeleteConfirmation();
});

/**
 * Initialize Leaflet map for jam location selection
 */
function initializeJamMap() {
  if (jamMap) {
    jamMap.invalidateSize();
    return;
  }

  const mapContainer = document.getElementById("jamMap");
  if (!mapContainer) {
    console.warn("Jam map container not found");
    return;
  }

  // Define bounds for Netherlands
  const maxBounds = L.latLngBounds(L.latLng(50.75, 3.5), L.latLng(53.7, 8.0));

  // Create map
  jamMap = L.map("jamMap", {
    maxBounds: maxBounds,
    maxBoundsViscosity: 1.0,
  }).setView([53.2194, 6.5665], 12);

  jamMap.setMaxBounds(maxBounds);

  // Add tile layer
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: "&copy; OpenStreetMap contributors",
    className: "map-tiles",
  }).addTo(jamMap);

  // Custom pin icon
  const pinIcon = L.icon({
    iconUrl: "/assets/pin-red.png",
    iconSize: [25, 40],
    iconAnchor: [12, 40],
    popupAnchor: [1, -34],
    shadowUrl: "/assets/pin-shadow.png",
    shadowSize: [41, 41],
    shadowAnchor: [13, 41],
  });

  // Map click handler
  jamMap.on("click", function (event) {
    const lat = event.latlng.lat.toFixed(6);
    const lng = event.latlng.lng.toFixed(6);

    // Update form fields
    document.getElementById("jamLat").value = lat;
    document.getElementById("jamLng").value = lng;

    // Remove previous marker
    if (jamMarker) {
      jamMap.removeLayer(jamMarker);
    }

    // Add new marker
    jamMarker = L.marker([lat, lng], { icon: pinIcon })
      .addTo(jamMap)
      .bindPopup("Geselecteerde locatie<br>Lat: " + lat + "<br>Lng: " + lng)
      .openPopup();
  });

  // Check if there are existing coordinates in form
  const existingLat = document.getElementById("jamLat").value;
  const existingLng = document.getElementById("jamLng").value;
  if (existingLat && existingLng) {
    jamMap.setView([parseFloat(existingLat), parseFloat(existingLng)], 15);
    jamMarker = L.marker([parseFloat(existingLat), parseFloat(existingLng)], {
      icon: pinIcon,
    }).addTo(jamMap);
  }
}

/**
 * Handle jam form submission
 */
async function handleJamFormSubmit(event) {
  event.preventDefault();

  const title = document.getElementById("jamTitle").value.trim();
  const description = document.getElementById("jamDescription").value.trim();
  const lat = parseFloat(document.getElementById("jamLat").value);
  const lng = parseFloat(document.getElementById("jamLng").value);
  const msgDiv = document.getElementById("jamMsg");

  // Validation
  if (!title || !description || !lat || !lng) {
    showMessage("Vul alstublieft alle velden in.", "danger", msgDiv);
    return;
  }

  if (isNaN(lat) || isNaN(lng)) {
    showMessage(
      "Ongeldige coördinaten. Klik op de kaart om de locatie te selecteren.",
      "danger",
      msgDiv,
    );
    return;
  }

  // Get user ID from the URL or data attribute
  const userIdElement = document.querySelector("[data-musician-id]");
  const userId = userIdElement
    ? userIdElement.getAttribute("data-musician-id")
    : getMuzikantUserIdFromPage();

  if (!userId) {
    showMessage("Gebruiker ID kon niet worden bepaald.", "danger", msgDiv);
    return;
  }

  const jamData = {
    title: title,
    description: description,
    lat: lat,
    lng: lng,
    muzikantUser: {
      id: parseInt(userId),
    },
  };

  try {
    showLoadingState(true);
    const response = await fetch("/api/jams/add", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(jamData),
    });

    if (response.ok) {
      const savedJam = await response.json();

      // Reset form
      document.getElementById("addJamForm").reset();
      document.getElementById("jamLat").value = "";
      document.getElementById("jamLng").value = "";

      // Clear marker
      if (jamMarker && jamMap) {
        jamMap.removeLayer(jamMarker);
        jamMarker = null;
      }

      showMessage("Jam succesvol aangemaakt!", "success", msgDiv);

      // Reload page after short delay
      setTimeout(() => {
        location.reload();
      }, 2000);
    } else {
      const errorData = await response.json().catch(() => ({}));
      showMessage(
        errorData.message ||
          "Er is iets fout gegaan bij het aanmaken van de jam.",
        "danger",
        msgDiv,
      );
    }
  } catch (error) {
    console.error("Error creating jam:", error);
    showMessage(
      "Er is een netwerkfout opgetreden. Probeer het later opnieuw.",
      "danger",
      msgDiv,
    );
  } finally {
    showLoadingState(false);
  }
}

/**
 * Get muzikant user ID from page elements
 */
function getMuzikantUserIdFromPage() {
  // Try to get from data attribute
  const dataAttr = document.querySelector("[data-user-id]");
  if (dataAttr) return dataAttr.getAttribute("data-user-id");

  // Try to extract from profile header
  const profileHeader = document.querySelector(".profile-header");
  if (profileHeader && profileHeader.dataset.userId) {
    return profileHeader.dataset.userId;
  }

  // Fallback: try to get from form or hidden input
  const hiddenInput = document.querySelector(
    'input[type="hidden"][name="muzikantUserId"]',
  );
  if (hiddenInput) return hiddenInput.value;

  return null;
}

/**
 * Show message in specific div
 */
function showMessage(message, type = "info", targetDiv) {
  if (!targetDiv) return;

  targetDiv.textContent = message;
  targetDiv.className = `mt-2 alert alert-${type}`;
  targetDiv.style.display = "block";

  // Auto-hide after 5 seconds for success messages
  if (type === "success") {
    setTimeout(() => {
      targetDiv.style.display = "none";
    }, 5000);
  }
}

/**
 * Show alert message
 */
function showAlert(message, type = "info") {
  const alertDiv = document.createElement("div");
  alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
  alertDiv.setAttribute("role", "alert");

  const icon = getAlertIcon(type);
  alertDiv.innerHTML = `
    ${icon} ${message}
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  `;

  const container = document.querySelector(".container");
  if (container) {
    container.insertBefore(alertDiv, container.firstChild);

    // Auto-dismiss after 5 seconds
    setTimeout(() => {
      const alert = bootstrap.Alert.getOrCreateInstance(alertDiv);
      if (alert) alert.close();
    }, 5000);
  }
}

/**
 * Get appropriate icon for alert type
 */
function getAlertIcon(type) {
  const icons = {
    success: '<i class="bi bi-check-circle"></i>',
    danger: '<i class="bi bi-exclamation-circle"></i>',
    warning: '<i class="bi bi-exclamation-triangle"></i>',
    info: '<i class="bi bi-info-circle"></i>',
  };
  return icons[type] || icons.info;
}

/**
 * Show/hide loading state on submit button
 */
function showLoadingState(isLoading) {
  const submitBtn = document.querySelector('#addJamForm button[type="submit"]');
  if (!submitBtn) return;

  if (isLoading) {
    submitBtn.disabled = true;
    submitBtn.innerHTML =
      '<span class="spinner-border spinner-border-sm me-2"></span>Aanmaken...';
  } else {
    submitBtn.disabled = false;
    submitBtn.innerHTML = "Jam Opslaan";
  }
}

/**
 * Setup delete confirmation dialogs
 */
function setupDeleteConfirmation() {
  const deleteForms = document.querySelectorAll(
    ".delete-form, .delete-jam-form",
  );
  deleteForms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      const message =
        this.getAttribute("data-confirm-message") ||
        "Weet je zeker dat je dit item wilt verwijderen?";
      if (!confirm(message)) {
        event.preventDefault();
      }
    });
  });
}

/**
 * Format coordinate for display
 */
function formatCoordinate(value, decimals = 4) {
  return parseFloat(value).toFixed(decimals);
}

/**
 * Utility: Copy to clipboard
 */
function copyToClipboard(text) {
  navigator.clipboard
    .writeText(text)
    .then(() => {
      showAlert("Gekopieerd naar klembord!", "success");
    })
    .catch(() => {
      showAlert("Kon niet naar klembord kopiëren.", "danger");
    });
}

/**
 * Refresh profile data
 */
function refreshProfile() {
  location.reload();
}

/**
 * Handle responsive map resize
 */
window.addEventListener("resize", () => {
  if (jamMap) {
    jamMap.invalidateSize();
  }
});
