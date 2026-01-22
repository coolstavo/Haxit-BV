// Parse data from hidden DOM elements
var initialEvents = [];
var initialLessons = [];
var initialJams = [];

document.querySelectorAll("#eventsData").forEach(function (el) {
  if (el.dataset.id) {
    initialEvents.push({
      id: parseInt(el.dataset.id),
      title: el.dataset.title || "",
      description: el.dataset.description || "",
      type: el.dataset.type || "",
      lat: parseFloat(el.dataset.lat),
      lng: parseFloat(el.dataset.lng),
    });
  }
});

document.querySelectorAll("#lessonsData").forEach(function (el) {
  if (el.dataset.id) {
    initialLessons.push({
      id: parseInt(el.dataset.id),
      instrument: el.dataset.instrument || "",
      rate: parseFloat(el.dataset.rate) || 0,
      rateType: el.dataset.ratetype || "",
      location: el.dataset.location || "",
      lat: parseFloat(el.dataset.lat),
      lng: parseFloat(el.dataset.lng),
      docentNaam: el.dataset.docentnaam || "Onbekend",
    });
  }
});

document.querySelectorAll("#jamsData").forEach(function (el) {
  if (el.dataset.id) {
    initialJams.push({
      id: parseInt(el.dataset.id),
      title: el.dataset.title || "",
      description: el.dataset.description || "",
      lat: parseFloat(el.dataset.lat),
      lng: parseFloat(el.dataset.lng),
    });
  }
});

var events = initialEvents;
var lessons = initialLessons;
var jams = initialJams;
var markers = {};
var eventIndex = events.length;

// Groningen and Drenthe
var maxBounds = L.latLngBounds(L.latLng(52.9, 6.0), L.latLng(53.6, 7.3));

var map = L.map("map", {
  maxBounds: maxBounds,
  maxBoundsViscosity: 1.0,
}).setView([53.2194, 6.5665], 13);

map.setMaxBounds(maxBounds);

// Oude map
var roads = L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
  maxZoom: 19,
  attribution: "&copy; OpenStreetMap-bijdragers",
}).addTo(map);

var customIcon = L.icon({
  iconUrl:
    typeof pinIconUrl !== "undefined" ? pinIconUrl : "/assets/pin-red.png",
  iconSize: [15, 40],
  iconAnchor: [15, 40],
  popupAnchor: [-6, -40],
  className: "custom-div-icon",
});

// Events op de kaart
if (Array.isArray(events) && events.length > 0) {
  events.forEach(function (event, index) {
    var lat = Number(event.lat);
    var lng = Number(event.lng);
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) return;

    var marker = L.marker([lat, lng], { icon: customIcon }).addTo(map);
    var eventUrl = "/event/" + event.id;
    var popupText =
      "<b><a href='" +
      eventUrl +
      "' style='color: inherit; text-decoration: none;'>" +
      event.title +
      "</a></b><br>" +
      "" +
      event.description +
      "<br>" +
      "<span class='badge rounded-pill navbar-brown'> event </span>";
    marker.bindPopup(popupText);

    markers["event-" + index] = marker;
  });
}

// Lessen op de kaart
if (Array.isArray(lessons) && lessons.length > 0) {
  lessons.forEach(function (lesson, index) {
    var lat = Number(lesson.lat);
    var lng = Number(lesson.lng);
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) return;

    var marker = L.marker([lat, lng], { icon: customIcon }).addTo(map);
    var lessonUrl = "/lesson/" + lesson.id;
    var popupText =
      "<b><a href='" +
      lessonUrl +
      "' style='color: inherit; text-decoration: none;'>" +
      lesson.instrument +
      " Les</a></b><br>" +
      "Docent: " +
      lesson.docentNaam +
      "<br>" +
      "€" +
      (lesson.rate ? lesson.rate.toFixed(2) : "0.00") +
      " " +
      lesson.rateType +
      "<br>" +
      (lesson.location ? "<small>" + lesson.location + "</small><br>" : "") +
      "<span class='badge rounded-pill bg-success'>Les</span>";
    marker.bindPopup(popupText);

    markers["lesson-" + index] = marker;
  });
}

// Jams op de kaart
if (Array.isArray(jams) && jams.length > 0) {
  jams.forEach(function (jam, index) {
    var lat = Number(jam.lat);
    var lng = Number(jam.lng);
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) return;

    var marker = L.marker([lat, lng], { icon: customIcon }).addTo(map);
    var jamUrl = "/jam/" + jam.id;
    var popupText =
      "<b><a href='" +
      jamUrl +
      "' style='color: inherit; text-decoration: none;'>" +
      jam.title +
      "</a></b><br>" +
      "" +
      jam.description +
      "<br>" +
      "<span class='badge rounded-pill bg-warning text-dark'>Jam</span>";
    marker.bindPopup(popupText);

    markers["jam-" + index] = marker;
  });
}

function focusOnMap(lat, lng, index) {
  var nLat = Number(lat);
  var nLng = Number(lng);
  if (Number.isFinite(nLat) && Number.isFinite(nLng)) {
    map.flyTo([nLat, nLng], 15);

    if (markers[index]) {
      setTimeout(function () {
        markers[index].openPopup();
      }, 500);
    }
  }
}

// click op map voor coordinaten
map.on("click", function (e) {
  var latField = document.getElementById("eventLat");
  var lngField = document.getElementById("eventLng");
  if (latField) latField.value = e.latlng.lat.toFixed(6);
  if (lngField) lngField.value = e.latlng.lng.toFixed(6);

  // Ook voor lesson formulier op docent pagina
  var lessonLatField = document.getElementById("lessonLat");
  var lessonLngField = document.getElementById("lessonLng");
  if (lessonLatField) lessonLatField.value = e.latlng.lat.toFixed(6);
  if (lessonLngField) lessonLngField.value = e.latlng.lng.toFixed(6);
});

// Event form submission
var addEventForm = document.getElementById("addEventForm");
if (addEventForm) {
  addEventForm.addEventListener("submit", function (e) {
    e.preventDefault();

    var formData = {
      title: document.getElementById("eventTitle").value,
      description: document.getElementById("eventDescription").value,
      type: document.getElementById("eventType").value,
      lat: parseFloat(document.getElementById("eventLat").value),
      lng: parseFloat(document.getElementById("eventLng").value),
      username: window.currentUsername,
    };

    // Send to backend
    fetch(
      "/api/events/add?username=" + encodeURIComponent(window.currentUsername),
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      },
    )
      .then((response) => response.json())
      .then((data) => {
        // toevoegen aan map
        var marker = L.marker([data.lat, data.lng], { icon: customIcon }).addTo(
          map,
        );
        var popupText =
          "<b>" +
          data.title +
          "</b><br>" +
          "" +
          data.description +
          "<br>" +
          "<span class='badge rounded-pill navbar-brown'>" +
          data.type +
          "</span>";
        marker.bindPopup(popupText);
        markers["event-" + eventIndex] = marker;

        // toevoegen aan lijst
        var eventCard = document.createElement("a");
        eventCard.href = "#";
        eventCard.className =
          "list-group-item list-group-item-action event-card border-0 shadow-sm mb-2 rounded";
        eventCard.dataset.lat = data.lat;
        eventCard.dataset.lng = data.lng;
        eventCard.dataset.index = eventIndex;
        eventCard.dataset.type = "event";
        eventCard.onclick = function () {
          focusOnMap(
            this.dataset.lat,
            this.dataset.lng,
            "event-" + this.dataset.index,
          );
        };
        eventCard.innerHTML =
          '<div class="d-flex w-100 justify-content-between">' +
          '<h5 class="mb-1 text-dark">' +
          data.title +
          "</h5>" +
          '<span class="badge rounded-pill navbar-brown">' +
          data.type +
          "</span>" +
          "</div>" +
          '<p class="mb-1">' +
          data.description +
          "</p>" +
          '<small class="text-muted">' +
          "Aangemaakt door <strong>" +
          data.user.username +
          "</strong>" +
          "</small>";
        '<div class="user-profile">' +
          "<strong>" +
          data.user.username +
          "</strong>" +
          "</div>";
        document.querySelector(".list-group").appendChild(eventCard);

        eventIndex++;

        bootstrap.Modal.getInstance(
          document.getElementById("addEventModal"),
        ).hide();
        addEventForm.reset();

        map.flyTo([data.lat, data.lng], 15);
        setTimeout(function () {
          marker.openPopup();
        }, 500);
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Er is een fout opgetreden bij het toevoegen van het event.");
      });
  });
}
