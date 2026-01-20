// Initialize location map for lesson view page
function initLessonLocationMap() {
  const latInput = document.getElementById("lessonLat");
  const lngInput = document.getElementById("lessonLng");
  const mapContainer = document.getElementById("lessonLocationMap");

  if (
    !mapContainer ||
    !latInput ||
    !lngInput ||
    !latInput.value ||
    !lngInput.value
  ) {
    return;
  }

  const lat = parseFloat(latInput.value);
  const lng = parseFloat(lngInput.value);

  if (isNaN(lat) || isNaN(lng)) {
    return;
  }

  const lessonMap = L.map("lessonLocationMap").setView([lat, lng], 15);

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

  L.marker([lat, lng], { icon: pinIcon })
    .addTo(lessonMap)
    .bindPopup("Les locatie")
    .openPopup();
}

// Initialize on DOM ready
document.addEventListener("DOMContentLoaded", initLessonLocationMap);
