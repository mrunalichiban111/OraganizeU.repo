document.addEventListener('DOMContentLoaded', function() {
    let currentDate = new Date();
    let currentView = 'month';
    let events = [];

    // DOM Elements
    const calendarDays = document.getElementById('calendarDays');
    const currentMonthElement = document.getElementById('currentMonth');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const viewButtons = document.querySelectorAll('.view-btn');
    const newEventBtn = document.getElementById('newEventBtn');
    const eventModal = document.getElementById('eventModal');
    const eventForm = document.getElementById('eventForm');
    const closeBtn = document.querySelector('.close-btn');
    const cancelBtn = document.getElementById('cancelEvent');

    // Event Listeners
    prevMonthBtn.addEventListener('click', () => navigateMonth(-1));
    nextMonthBtn.addEventListener('click', () => navigateMonth(1));
    viewButtons.forEach(btn => {
        btn.addEventListener('click', () => changeView(btn.dataset.view));
    });
    newEventBtn.addEventListener('click', () => showEventModal());
    closeBtn.addEventListener('click', () => hideEventModal());
    cancelBtn.addEventListener('click', () => hideEventModal());
    eventForm.addEventListener('submit', handleEventSubmit);

    // Initialize calendar
    renderCalendar();

    function navigateMonth(direction) {
        currentDate.setMonth(currentDate.getMonth() + direction);
        renderCalendar();
    }

    function changeView(view) {
        currentView = view;
        viewButtons.forEach(btn => {
            btn.classList.toggle('active', btn.dataset.view === view);
        });
        renderCalendar();
    }

    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        
        // Update month/year display
        currentMonthElement.textContent = new Date(year, month).toLocaleString('default', { 
            month: 'long', 
            year: 'numeric' 
        });

        // Clear previous calendar
        calendarDays.innerHTML = '';

        // Get first day of month and total days
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const totalDays = lastDay.getDate();
        const startingDay = firstDay.getDay();

        // Add days from previous month
        const prevMonthLastDay = new Date(year, month, 0).getDate();
        for (let i = startingDay - 1; i >= 0; i--) {
            const dayElement = createDayElement(prevMonthLastDay - i, 'other-month');
            calendarDays.appendChild(dayElement);
        }

        // Add days of current month
        for (let day = 1; day <= totalDays; day++) {
            const dayElement = createDayElement(day, isToday(year, month, day) ? 'today' : '');
            calendarDays.appendChild(dayElement);
        }

        // Add days from next month
        const remainingDays = 42 - (startingDay + totalDays); // 42 = 6 rows * 7 days
        for (let day = 1; day <= remainingDays; day++) {
            const dayElement = createDayElement(day, 'other-month');
            calendarDays.appendChild(dayElement);
        }

        // Render events
        renderEvents();
    }

    function createDayElement(day, className) {
        const dayElement = document.createElement('div');
        dayElement.className = `calendar-day ${className}`;
        dayElement.textContent = day;
        return dayElement;
    }

    function isToday(year, month, day) {
        const today = new Date();
        return today.getFullYear() === year && 
               today.getMonth() === month && 
               today.getDate() === day;
    }

    function renderEvents() {
        const dayElements = document.querySelectorAll('.calendar-day');
        dayElements.forEach(dayElement => {
            const day = parseInt(dayElement.textContent);
            const eventsForDay = getEventsForDay(day);
            
            eventsForDay.forEach(event => {
                const eventElement = document.createElement('div');
                eventElement.className = `event ${event.recurring ? 'recurring' : ''}`;
                eventElement.textContent = event.title;
                eventElement.addEventListener('click', () => showEventModal(event));
                dayElement.appendChild(eventElement);
            });
        });
    }

    function getEventsForDay(day) {
        // Filter events for the current day
        return events.filter(event => {
            const eventDate = new Date(event.startAt);
            return eventDate.getDate() === day && 
                   eventDate.getMonth() === currentDate.getMonth() &&
                   eventDate.getFullYear() === currentDate.getFullYear();
        });
    }

    function showEventModal(event = null) {
        eventModal.style.display = 'block';
        
        if (event) {
            // Edit existing event
            document.getElementById('eventTitle').value = event.title;
            document.getElementById('eventDescription').value = event.description;
            document.getElementById('eventLocation').value = event.location;
            document.getElementById('eventStart').value = formatDateTime(event.startAt);
            document.getElementById('eventEnd').value = formatDateTime(event.endAt);
            document.getElementById('eventAllDay').checked = event.allDay;
            document.getElementById('eventRecurrence').value = event.recurrenceRule || '';
        } else {
            // New event
            eventForm.reset();
            const now = new Date();
            document.getElementById('eventStart').value = formatDateTime(now);
            document.getElementById('eventEnd').value = formatDateTime(new Date(now.getTime() + 3600000));
        }
    }

    function hideEventModal() {
        eventModal.style.display = 'none';
        eventForm.reset();
    }

    function formatDateTime(date) {
        const d = new Date(date);
        return d.toISOString().slice(0, 16);
    }

    async function handleEventSubmit(e) {
        e.preventDefault();

        const eventData = {
            title: document.getElementById('eventTitle').value,
            description: document.getElementById('eventDescription').value,
            location: document.getElementById('eventLocation').value,
            startAt: document.getElementById('eventStart').value,
            endAt: document.getElementById('eventEnd').value,
            allDay: document.getElementById('eventAllDay').checked,
            recurrenceRule: document.getElementById('eventRecurrence').value
        };

        try {
            const response = await fetch('/api/events', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(eventData)
            });

            if (response.ok) {
                const newEvent = await response.json();
                events.push(newEvent);
                renderCalendar();
                hideEventModal();
            } else {
                alert('Failed to create event');
            }
        } catch (error) {
            console.error('Error creating event:', error);
            alert('Error creating event');
        }
    }

    // Load initial events
    async function loadEvents() {
        try {
            const start = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
            const end = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
            
            const response = await fetch(`/api/events?start=${start.toISOString()}&end=${end.toISOString()}`);
            if (response.ok) {
                events = await response.json();
                renderCalendar();
            }
        } catch (error) {
            console.error('Error loading events:', error);
        }
    }

    loadEvents();
}); 