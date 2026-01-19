// Initialize map for docent lesson creation
function initDocentLessonMap() {
  const mapContainer = document.getElementById("lessonMap");

  if (!mapContainer) {
    return;
  }

  let lessonMap = null;
  let lessonMarker = null;

  const maxBounds = L.latLngBounds(L.latLng(52.9, 6.0), L.latLng(53.6, 7.3));

  lessonMap = L.map("lessonMap", {
    maxBounds: maxBounds,
    maxBoundsViscosity: 1.0,
  }).setView([53.2194, 6.5665], 12);

  lessonMap.setMaxBounds(maxBounds);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: "&copy; OpenStreetMap-bijdragers",
  }).addTo(lessonMap);

  const pinIcon = L.icon({
    iconUrl: "/assets/pin-red.png",
    iconSize: [15, 40],
    iconAnchor: [15, 40],
    popupAnchor: [-6, -40],
  });

  // Click handler for map
  lessonMap.on("click", function (e) {
    const lat = e.latlng.lat.toFixed(6);
    const lng = e.latlng.lng.toFixed(6);

    document.getElementById("lessonLat").value = lat;
    document.getElementById("lessonLng").value = lng;

    // Remove old marker if exists
    if (lessonMarker) {
      lessonMap.removeLayer(lessonMarker);
    }

    // Add new marker
    lessonMarker = L.marker([e.latlng.lat, e.latlng.lng], {
      icon: pinIcon,
    }).addTo(lessonMap);
    lessonMarker.bindPopup("Les locatie").openPopup();
  });
}

// Initialize on DOM ready
document.addEventListener("DOMContentLoaded", initDocentLessonMap);
