// static/js/Schedule.js

// Entry point
document.addEventListener("DOMContentLoaded", () => {
  // CSRF
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

  // DOM
  const daysGrid = document.getElementById("days-grid");
  const weekDays = document.getElementById("week-days");
  const dayColumn = document.getElementById("day-column");
  const currentDateElement = document.getElementById("current-date");
  const prevBtn = document.getElementById("prev-btn");
  const nextBtn = document.getElementById("next-btn");
  const todayBtn = document.getElementById("today-btn");
  const viewBtns = document.querySelectorAll(".view-btn");
  const calendarViews = document.querySelectorAll(".calendar-view");

  // Modal
  const modalOverlay = document.getElementById("event-modal-overlay");
  const closeModalBtn = document.getElementById("close-modal");
  const eventForm = document.getElementById("event-form");
  const eventTitleInput = document.getElementById("event-title");
  const eventDateInput = document.getElementById("event-date");
  const eventStartInput = document.getElementById("event-start");
  const eventEndInput = document.getElementById("event-end");
  const eventStatusInput = document.getElementById("event-status");
  const eventIdInput = document.getElementById("event-id");
  const saveEventBtn = document.getElementById("save-event");
  const cancelEventBtn = document.getElementById("cancel-event");
  const deleteEventBtn = document.getElementById("delete-event");
  const modalTitle = document.getElementById("modal-title");

  // State
  let currentDate = new Date();
  let currentView = "month";
  let events = [];

  const API_BASE = "/api/events";

  // INITIALIZATION
  async function initCalendar() {
    await renderCalendar();
    setupEventListeners();
  }

  // HELPERS
  function formatDate(date) { return date.toISOString().split('T')[0]; }
  function formatTime(ts) {
    const [h, m] = ts.split(':').map(Number);
    const period = h >= 12 ? 'PM' : 'AM';
    const hh = h % 12 || 12;
    return `${hh}:${m.toString().padStart(2, '0')} ${period}`;
  }
  function getStartOfWeek(d) { const dt = new Date(d); dt.setDate(dt.getDate() - dt.getDay()); return dt; }
  function isSameDay(a, b) { return formatDate(a) === formatDate(b); }
  function isInSameWeek(d, s) { const e = new Date(s); e.setDate(e.getDate() + 6); return d >= s && d <= e; }
  function getDayDifference(a, b) { return Math.floor((b - a) / (1000 * 60 * 60 * 24)); }
  function getViewStartDate() {
    if (currentView === 'month') return new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
    if (currentView === 'week') return getStartOfWeek(currentDate);
    return currentDate;
  }
  function getViewEndDate() {
    if (currentView === 'month') return new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
    if (currentView === 'week') { const e = new Date(getStartOfWeek(currentDate)); e.setDate(e.getDate() + 6); return e; }
    return currentDate;
  }

  // DATA
  async function loadEvents() {
    const start = getViewStartDate(), end = getViewEndDate();
    const qs = `?start=${formatDate(start)}&end=${formatDate(end)}`;
    try {
      const res = await fetch(API_BASE + qs, { method: 'GET', credentials: 'same-origin', headers: { [csrfHeader]: csrfToken } });
      if (!res.ok) throw new Error(`Status ${res.status}`);
      events = await res.json();
    } catch (e) { console.error('Error loading events:', e); events = []; }
  }

  // RENDER
  async function renderCalendar() {
    await loadEvents();
    if (currentView === 'month') renderMonthView();
    if (currentView === 'week') renderWeekView();
    if (currentView === 'day') renderDayView();
    updateCurrentDateDisplay();
  }

  function updateCurrentDateDisplay() {
    const opts = { year: 'numeric', month: 'long' };
    if (currentView === 'week') {
      const ws = getStartOfWeek(currentDate), we = new Date(ws); we.setDate(we.getDate() + 6);
      const sm = ws.toLocaleString('default', { month: 'short' }), em = we.toLocaleString('default', { month: 'short' });
      currentDateElement.textContent = sm === em ? `${sm} ${ws.getDate()} - ${we.getDate()}, ${ws.getFullYear()}` : `${sm} ${ws.getDate()} - ${em} ${we.getDate()}, ${ws.getFullYear()}`;
    } else if (currentView === 'day') {
      currentDateElement.textContent = currentDate.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' });
    } else {
      currentDateElement.textContent = currentDate.toLocaleDateString('en-US', opts);
    }
  }

  // Month View
  function renderMonthView() {
    daysGrid.innerHTML = '';
    const y = currentDate.getFullYear(), m = currentDate.getMonth();
    const fd = new Date(y, m, 1), ld = new Date(y, m + 1, 0), sd = fd.getDay(), dm = ld.getDate(), pld = new Date(y, m, 0).getDate();
    for (let i = sd - 1; i >= 0; i--) daysGrid.appendChild(createDayCell(pld - i, 'other-month'));
    const today = new Date();
    for (let d = 1; d <= dm; d++) {
      const isT = d === today.getDate() && m === today.getMonth() && y === today.getFullYear();
      const cell = createDayCell(d, isT ? 'today' : '');
      const dt = new Date(y, m, d); cell.dataset.date = formatDate(dt);
      addEventsToDayCell(cell, dt); daysGrid.appendChild(cell);
    }
    const total = Math.ceil((sd + dm) / 7) * 7, nd = total - (sd + dm);
    for (let i = 1; i <= nd; i++) daysGrid.appendChild(createDayCell(i, 'other-month'));
  }
  function createDayCell(day, cls) { const c = document.createElement('div'); c.className = `day-cell ${cls}`; const n = document.createElement('div'); n.className = 'day-number'; n.textContent = day; c.appendChild(n); return c; }
  function addEventsToDayCell(cell, date) { const ds = formatDate(date); events.filter(e => e.date === ds).forEach(evt => { const el = document.createElement('div'); el.className = `event ${evt.status}`; el.textContent = evt.title; el.dataset.id = evt.id; if (evt.startTime) { const ts = document.createElement('span'); ts.className = 'event-time'; ts.textContent = ` (${formatTime(evt.startTime)})`; el.appendChild(ts); } cell.appendChild(el); }); }

  // Week View
  function renderWeekView() {
    weekDays.innerHTML = '';
    const start = getStartOfWeek(currentDate);
    for (let i = 0; i < 7; i++) {
      const dt = new Date(start); dt.setDate(dt.getDate() + i);
      const col = document.createElement('div'); col.className = 'week-day-column';
      const hdr = document.createElement('div'); hdr.className = 'week-day-header';
      const nm = dt.toLocaleDateString('en-US', { weekday: 'short' }), num = dt.getDate();
      hdr.innerHTML = `<div>${nm}</div><div class='${isSameDay(dt, new Date()) ? 'today' : ''}'>${num}</div>`;
      col.appendChild(hdr);
      const slots = document.createElement('div'); slots.className = 'week-day-slots';
      for (let h = 0; h < 24; h++) { const s = document.createElement('div'); s.className = 'week-time-slot'; s.dataset.hour = h; s.dataset.date = formatDate(dt); slots.appendChild(s); }
      col.appendChild(slots); weekDays.appendChild(col);
    }
    addEventsToWeekView();
  }
  function addEventsToWeekView() { const start = getStartOfWeek(currentDate); events.forEach(evt => { const d = new Date(evt.date); if (isInSameWeek(d, start) && evt.startTime && evt.endTime) { const di = getDayDifference(start, d); const col = weekDays.children[di]; const slots = col.querySelector('.week-day-slots'); const [sh, sm] = evt.startTime.split(':').map(Number), [eh, em] = evt.endTime.split(':').map(Number); const slot = slots.children[sh]; if (slot) { const el = document.createElement('div'); el.className = `event ${evt.status}`; el.textContent = evt.title; el.dataset.id = evt.id; const top = (sm / 60) * 100; height = (eh - sh + (em - sm) / 60) * 60; el.style.top = `${top}%`; el.style.height = `${height}px`; slot.appendChild(el); } } }); }

  // Day View
  function renderDayView() {
    dayColumn.innerHTML = '';
    const hdr = document.createElement('div'); hdr.className = 'day-header';
    const nm = currentDate.toLocaleDateString('en-US', { weekday: 'long' }), num = currentDate.getDate();
    hdr.innerHTML = `<div>${nm}</div><div class='${isSameDay(currentDate, new Date()) ? 'today' : ''}'>${num}</div>`;
    dayColumn.appendChild(hdr);
    const slots = document.createElement('div'); slots.className = 'day-slots';
    for (let h = 0; h < 24; h++) { const s = document.createElement('div'); s.className = 'day-time-slot'; s.dataset.hour = h; s.dataset.date = formatDate(currentDate); slots.appendChild(s); }
    dayColumn.appendChild(slots);
    addEventsToDayView();
  }
  function addEventsToDayView() { const ds = formatDate(currentDate), slots = dayColumn.querySelector('.day-slots'); events.filter(e => e.date === ds && e.startTime && e.endTime).forEach(evt => { const [sh, sm] = evt.startTime.split(':').map(Number), [eh, em] = evt.endTime.split(':').map(Number); const slot = slots.children[sh]; if (slot) { const el = document.createElement('div'); el.className = `event ${evt.status}`; el.textContent = evt.title; el.dataset.id = evt.id; const top = (sm / 60) * 100; height = (eh - sh + (em - sm) / 60) * 60; el.style.top = `${top}%`; el.style.height = `${height}px`; slot.appendChild(el); } }); }

  // MODAL
  function openAddEventModal(date, hour = null) { modalTitle.textContent = 'Add Event'; eventForm.reset(); eventDateInput.value = date; if (hour !== null) { const h = hour.toString().padStart(2, '0'); eventStartInput.value = `${h}:00`; eventEndInput.value = `${((+hour + 1) % 24).toString().padStart(2, '0')}:00`; } eventIdInput.value = ''; deleteEventBtn.style.display = 'none'; modalOverlay.classList.add('active'); }
  function openEditEventModal(evt) { modalTitle.textContent = 'Edit Event'; eventTitleInput.value = evt.title; eventDateInput.value = evt.date; eventStartInput.value = evt.startTime || ''; eventEndInput.value = evt.endTime || ''; eventStatusInput.value = evt.status; eventIdInput.value = evt.id; deleteEventBtn.style.display = 'block'; modalOverlay.classList.add('active'); }
  function closeModal() { modalOverlay.classList.remove('active'); selectedEvent = null; }

  // CRUD
  async function saveEvent(e) {
    e.preventDefault();
    if (!eventForm.checkValidity()) return;

    const startTime = eventStartInput.value;
    const endTime = eventEndInput.value;

    // Validate that startTime is before endTime
    if (startTime && endTime) {
      const start = new Date(`1970-01-01T${startTime}:00`);
      const end = new Date(`1970-01-01T${endTime}:00`);
      if (start >= end) {
        alert("Start time must be before end time");
        return;
      }
    }

    const dto = {
      id: eventIdInput.value || null,
      title: eventTitleInput.value,
      date: eventDateInput.value,
      startTime: startTime,
      endTime: endTime,
      status: eventStatusInput.value.toUpperCase()
    };

    const method = dto.id ? 'PUT' : 'POST';
    const url = dto.id ? `${API_BASE}/${dto.id}` : API_BASE;

    try {
      const res = await fetch(url, {
        method,
        credentials: 'same-origin',
        headers: {
          'Content-Type': 'application/json',
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify(dto)
      });

      if (!res.ok) {
        const errorMessage = await res.text();
        throw new Error(`Status ${res.status}: ${errorMessage}`);
      }

      closeModal();
      await renderCalendar();
    } catch (err) {
      console.error('Error saving event:', err);
      alert(`Save failed: ${err.message}`);
    }
  }

  async function deleteEvent() { const id = eventIdInput.value; if (!id) return; try { const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE', credentials: 'same-origin', headers: { [csrfHeader]: csrfToken } }); if (!res.ok) throw new Error(`Status ${res.status}`); closeModal(); await renderCalendar(); } catch (err) { console.error('Error deleting event:', err); alert(`Delete failed: ${err.message}`); } }

  // EVENTS
  function setupEventListeners() { prevBtn.addEventListener('click', () => navigate('prev')); nextBtn.addEventListener('click', () => navigate('next')); todayBtn.addEventListener('click', () => { currentDate = new Date(); renderCalendar(); }); viewBtns.forEach(btn => btn.addEventListener('click', () => { currentView = btn.dataset.view; viewBtns.forEach(b => b.classList.remove('active')); btn.classList.add('active'); calendarViews.forEach(v => v.classList.remove('active')); document.querySelector(`.${currentView}-view`).classList.add('active'); renderCalendar(); })); daysGrid.addEventListener('click', onGridClick); weekDays.addEventListener('click', onGridClick); dayColumn.addEventListener('click', onGridClick); closeModalBtn.addEventListener('click', closeModal); modalOverlay.addEventListener('click', e => { if (e.target === modalOverlay) closeModal(); }); cancelEventBtn.addEventListener('click', closeModal); saveEventBtn.addEventListener('click', saveEvent); deleteEventBtn.addEventListener('click', deleteEvent); }
  function onGridClick(e) { const ev = e.target.closest('.event'), cell = e.target.closest('.day-cell, .week-time-slot, .day-time-slot'); if (ev) { selectedEvent = events.find(x => x.id === ev.dataset.id); if (selectedEvent) openEditEventModal(selectedEvent); } else if (cell && cell.dataset.date) { selectedDate = cell.dataset.date; openAddEventModal(selectedDate, cell.dataset.hour || null); } }
  function navigate(dir) { if (currentView === 'month') currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + (dir === 'prev' ? -1 : 1), 1); else if (currentView === 'week') currentDate.setDate(currentDate.getDate() + (dir === 'prev' ? -7 : 7)); else currentDate.setDate(currentDate.getDate() + (dir === 'prev' ? -1 : 1)); renderCalendar(); }

  // Start
  initCalendar();
});
