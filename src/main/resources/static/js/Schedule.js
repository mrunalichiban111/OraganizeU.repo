console.log("Schedule.js loaded!");

document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const monthGrid = document.getElementById('monthGrid');
    const weekGrid = document.getElementById('weekGrid');
    const dayGrid = document.getElementById('dayGrid');
    const currentPeriodDisplay = document.getElementById('currentPeriod');
    const prevPeriodBtn = document.getElementById('prevPeriod');
    const nextPeriodBtn = document.getElementById('nextPeriod');
    const monthViewBtn = document.getElementById('monthViewBtn');
    const weekViewBtn = document.getElementById('weekViewBtn');
    const dayViewBtn = document.getElementById('dayViewBtn');
    const eventForm = document.getElementById('eventForm');
    const eventIdInput = document.getElementById('eventId');
    const eventTitleInput = document.getElementById('eventTitle');
    const eventDescriptionInput = document.getElementById('eventDescription');
    const eventStartInput = document.getElementById('eventStart');
    const eventEndInput = document.getElementById('eventEnd');
    const eventLocationInput = document.getElementById('eventLocation');
    const deleteEventBtn = document.getElementById('deleteEventBtn');
    const eventDetails = document.getElementById('eventDetails');

    // State
    let currentView = 'week';
    let currentDate = new Date();
    let events = [];
    let selectedEvent = null;
    const API_BASE = '/api/events';

    // Initialize the view
    renderCurrentView();

    // Event Listeners
    prevPeriodBtn.addEventListener('click', () => {
        if (currentView === 'month') {
            currentDate.setMonth(currentDate.getMonth() - 1);
        } else if (currentView === 'week') {
            currentDate.setDate(currentDate.getDate() - 7);
        } else {
            currentDate.setDate(currentDate.getDate() - 1);
        }
        renderCurrentView();
    });

    nextPeriodBtn.addEventListener('click', () => {
        if (currentView === 'month') {
            currentDate.setMonth(currentDate.getMonth() + 1);
        } else if (currentView === 'week') {
            currentDate.setDate(currentDate.getDate() + 7);
        } else {
            currentDate.setDate(currentDate.getDate() + 1);
        }
        renderCurrentView();
    });

    monthViewBtn.addEventListener('click', () => {
        currentView = 'month';
        updateViewButtons();
        renderCurrentView();
    });

    weekViewBtn.addEventListener('click', () => {
        currentView = 'week';
        updateViewButtons();
        renderCurrentView();
    });

    dayViewBtn.addEventListener('click', () => {
        currentView = 'day';
        updateViewButtons();
        renderCurrentView();
    });

    // Event Form Logic
    if (eventForm) {
        eventForm.onsubmit = async (e) => {
            e.preventDefault();
            
            const formData = new FormData(eventForm);
            const eventData = {
                id: eventIdInput ? eventIdInput.value : null,
                title: formData.get('title'),
                description: formData.get('description'),
                location: formData.get('location'),
                startAt: fromLocalDateTime(formData.get('startAt')),
                endAt: fromLocalDateTime(formData.get('endAt')),
                status: 'UPCOMING'
            };

            await saveEvent(eventData);
        };
    }

    // Delete event handler
    deleteEventBtn.addEventListener('click', async () => {
        if (!eventIdInput.value) return;
        
        if (confirm('Are you sure you want to delete this event?')) {
            try {
                await deleteEvent(eventIdInput.value);
                events = events.filter(evt => evt.id !== parseInt(eventIdInput.value));
                renderCurrentView();
                closeEventForm();
            } catch (error) {
                console.error('Error deleting event:', error);
                alert('Failed to delete event. Please try again.');
            }
        }
    });

    // Helper Functions
    async function fetchEvents(rangeStart, rangeEnd) {
        // Convert local dates to UTC while preserving the intended time
        const start = new Date(rangeStart);
        const end = new Date(rangeEnd);
        
        // Format dates to ISO strings
        const startISO = start.toISOString();
        const endISO = end.toISOString();
        
        const url = `${API_BASE}?start=${encodeURIComponent(startISO)}&end=${encodeURIComponent(endISO)}`;
        console.log('Fetching events from:', url);
        
        try {
            const res = await fetch(url);
            if (!res.ok) {
                throw new Error(`Failed to fetch events: ${res.status} ${res.statusText}`);
            }
            events = await res.json();
            // Convert UTC dates back to local time for display
            events = events.map(evt => ({
                ...evt,
                startAt: new Date(evt.startAt).toISOString(),
                endAt: new Date(evt.endAt).toISOString()
            }));
            console.log('Fetched events:', events);
        } catch (error) {
            console.error('Error fetching events:', error);
            throw error;
        }
    }

    async function saveEvent(eventData) {
        if (!eventData || !eventData.startAt || !eventData.endAt) {
            console.error('Invalid event data:', eventData);
            alert('Invalid event data. Please check all fields.');
            return;
        }

        if (eventData.startAt >= eventData.endAt) {
            alert('Start time must be before end time');
            return;
        }

        const url = eventData.id ? `${API_BASE}/${eventData.id}` : API_BASE;
        const method = eventData.id ? 'PUT' : 'POST';
        
        console.log('Sending event data to server:', eventData);

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({
                    id: eventData.id,
                    title: eventData.title,
                    description: eventData.description || '',
                    location: eventData.location || '',
                    startAt: toLocalDateTime(eventData.startAt),
                    endAt: toLocalDateTime(eventData.endAt),
                    status: eventData.status || 'UPCOMING'
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Failed to save event: ${response.status}`);
            }

            const data = await response.json();
            console.log('Server response:', data);
            
            await loadEvents();
            
            if (eventDetails) {
                eventDetails.style.display = 'none';
            }
            
            if (eventForm) {
                eventForm.reset();
            }
            
            selectedEvent = null;
        } catch (error) {
            console.error('Error saving event:', error);
            alert(error.message || 'Failed to save event. Please try again.');
        }
    }

    async function deleteEvent(eventId) {
        if (!eventId) {
            console.error('Invalid event ID');
            return;
        }

        if (!confirm('Are you sure you want to delete this event?')) {
            return;
        }

        const url = `${API_BASE}/${eventId}`;
        console.log('Deleting event:', url);

        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Failed to delete event: ${response.status}`);
            }

            console.log('Event deleted successfully');
            
            await loadEvents();
            
            if (eventDetails) {
                eventDetails.style.display = 'none';
            }
            
            if (eventForm) {
                eventForm.reset();
            }
            
            selectedEvent = null;
        } catch (error) {
            console.error('Error deleting event:', error);
            alert(error.message || 'Failed to delete event. Please try again.');
        }
    }

    function renderEvent(evt, cell) {
        if (!evt || !cell) return;
        
        console.log('Rendering event block:', {
            title: evt.title,
            start: evt.startAt,
            end: evt.endAt
        });
        
        const block = document.createElement('div');
        block.className = 'event-block';
        
        const startTime = new Date(evt.startAt);
        const endTime = new Date(evt.endAt);
        
        // Calculate duration in hours
        const duration = (endTime - startTime) / (1000 * 60 * 60);
        
        // Set block height based on duration
        block.style.height = `${Math.max(30, duration * 60)}px`; // Minimum 30px height
        block.style.minHeight = '30px';
        
        // Format time for display
        const startTimeStr = formatTime(startTime);
        const endTimeStr = formatTime(endTime);
        block.textContent = `${evt.title}\n${startTimeStr} - ${endTimeStr}`;
        block.style.backgroundColor = '#8c52ff';
        block.style.color = 'white';
        block.style.padding = '4px';
        block.style.borderRadius = '4px';
        block.style.margin = '2px';
        block.style.overflow = 'hidden';
        block.style.whiteSpace = 'nowrap';
        block.style.textOverflow = 'ellipsis';
        
        block.onclick = (e) => {
            e.stopPropagation();
            selectedEvent = evt;
            if (eventIdInput) eventIdInput.value = evt.id;
            if (eventTitleInput) eventTitleInput.value = evt.title;
            if (eventDescriptionInput) eventDescriptionInput.value = evt.description || '';
            if (eventStartInput) eventStartInput.value = evt.startAt.slice(0, 16);
            if (eventEndInput) eventEndInput.value = evt.endAt.slice(0, 16);
            if (eventLocationInput) eventLocationInput.value = evt.location || '';
            if (deleteEventBtn) {
                deleteEventBtn.style.display = 'block';
            }
            if (eventDetails) eventDetails.style.display = 'block';
        };
        
        cell.appendChild(block);
    }

    function closeEventForm() {
        if (eventForm) {
            eventForm.reset();
            if (eventIdInput) eventIdInput.value = '';
            if (deleteEventBtn) deleteEventBtn.style.display = 'none';
            if (eventDetails) eventDetails.style.display = 'none';
            selectedEvent = null;
        }
    }

    function updateViewButtons() {
        monthViewBtn.classList.toggle('active', currentView === 'month');
        weekViewBtn.classList.toggle('active', currentView === 'week');
        dayViewBtn.classList.toggle('active', currentView === 'day');
        
        monthGrid.style.display = currentView === 'month' ? 'grid' : 'none';
        weekGrid.style.display = currentView === 'week' ? 'grid' : 'none';
        dayGrid.style.display = currentView === 'day' ? 'grid' : 'none';
    }

    async function renderCurrentView() {
        let startDate, endDate;
        
        if (currentView === 'month') {
            startDate = getStartOfMonth(currentDate);
            endDate = getEndOfMonth(currentDate);
        } else if (currentView === 'week') {
            startDate = getStartOfWeek(currentDate);
            endDate = getEndOfWeek(currentDate);
        } else { // day view
            startDate = new Date(currentDate);
            startDate.setHours(0, 0, 0, 0);
            endDate = new Date(currentDate);
            endDate.setHours(23, 59, 59, 999);
        }

        try {
            await fetchEvents(startDate, endDate);
            
            if (currentView === 'month' && monthGrid) {
                const dates = getMonthDates(currentDate);
                createGridCells(monthGrid, dates);
                renderEvents(monthGrid, dates);
                if (currentPeriodDisplay) {
                    currentPeriodDisplay.textContent = currentDate.toLocaleDateString('en-US', { 
                        month: 'long',
                        year: 'numeric'
                    });
                }
            } else if (currentView === 'week' && weekGrid) {
                const dates = getWeekDates(currentDate);
                createGridCells(weekGrid, dates);
                renderEvents(weekGrid, dates);
                if (currentPeriodDisplay) {
                    const weekStart = dates[0];
                    const weekEnd = dates[dates.length - 1];
                    currentPeriodDisplay.textContent = `${weekStart.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} - ${weekEnd.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}`;
                }
            } else if (currentView === 'day' && dayGrid) {
                const dates = [currentDate];
                createGridCells(dayGrid, dates);
                renderEvents(dayGrid, dates);
                if (currentPeriodDisplay) {
                    currentPeriodDisplay.textContent = currentDate.toLocaleDateString('en-US', { 
                        weekday: 'long',
                        month: 'long',
                        day: 'numeric',
                        year: 'numeric'
                    });
                }
            }
            
            updateViewButtons();
        } catch (error) {
            console.error('Error rendering view:', error);
            alert('Failed to load events. Please try again.');
        }
    }

    // Date helper functions
    function getStartOfWeek(date) {
        const start = new Date(date);
        start.setDate(start.getDate() - start.getDay());
        return start;
    }

    function getEndOfWeek(date) {
        const end = new Date(date);
        end.setDate(end.getDate() + (6 - end.getDay()));
        return end;
    }

    function getStartOfMonth(date) {
        return new Date(date.getFullYear(), date.getMonth(), 1);
    }

    function getEndOfMonth(date) {
        return new Date(date.getFullYear(), date.getMonth() + 1, 0);
    }

    function formatTime(date) {
        return date.toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            hour12: true,
            timeZone: 'Asia/Kolkata' // Use IST
        });
    }

    function getWeekDates(date) {
        const start = getStartOfWeek(date);
        const dates = [];
        for (let i = 0; i < 7; i++) {
            const current = new Date(start);
            current.setDate(start.getDate() + i);
            dates.push(current);
        }
        return dates;
    }

    function getMonthDates(date) {
        const year = date.getFullYear();
        const month = date.getMonth();
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const dates = [];
        
        // Add days from previous month
        const firstDayOfWeek = firstDay.getDay();
        for (let i = firstDayOfWeek - 1; i >= 0; i--) {
            const prevDate = new Date(year, month, -i);
            dates.push(prevDate);
        }
        
        // Add days of current month
        for (let i = 1; i <= lastDay.getDate(); i++) {
            dates.push(new Date(year, month, i));
        }
        
        // Add days from next month
        const remainingDays = 42 - dates.length; // 6 rows * 7 days
        for (let i = 1; i <= remainingDays; i++) {
            dates.push(new Date(year, month + 1, i));
        }
        
        return dates;
    }

    function createGridCells(grid, dates) {
        if (!grid) return;
        grid.innerHTML = '';

        if (currentView === 'month') {
            grid.style.gridTemplateColumns = 'repeat(7, 1fr)';
            grid.style.gridTemplateRows = '40px repeat(6, 1fr)';
            
            const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
            days.forEach(day => {
                const dayLabel = document.createElement('div');
                dayLabel.className = 'day-label';
                dayLabel.textContent = day;
                grid.appendChild(dayLabel);
            });
            
            dates.forEach(date => {
                const cell = document.createElement('div');
                cell.className = 'cell';
                cell.dataset.date = date.toISOString();
                
                const dateNum = document.createElement('div');
                dateNum.className = 'date-number';
                dateNum.textContent = date.getDate();
                cell.appendChild(dateNum);
                
                cell.onclick = () => {
                    // Create dates in IST
                    const startDate = new Date(date);
                    startDate.setHours(0, 0, 0, 0);
                    const endDate = new Date(date);
                    endDate.setHours(23, 59, 59);
                    
                    // Clear any existing event selection
                    selectedEvent = null;
                    if (eventIdInput) eventIdInput.value = '';
                    
                    // Set form values
                    if (eventTitleInput) eventTitleInput.value = '';
                    if (eventDescriptionInput) eventDescriptionInput.value = '';
                    if (eventLocationInput) eventLocationInput.value = '';
                    if (eventStartInput) eventStartInput.value = startDate.toISOString().slice(0, 16);
                    if (eventEndInput) eventEndInput.value = endDate.toISOString().slice(0, 16);
                    
                    // Show form
                    if (deleteEventBtn) deleteEventBtn.style.display = 'none';
                    if (eventDetails) eventDetails.style.display = 'block';
                };
                
                grid.appendChild(cell);
            });
        } else {
            // Create empty cell for top-left corner
            const emptyCell = document.createElement('div');
            emptyCell.className = 'empty-cell';
            grid.appendChild(emptyCell);

            // Create day labels
            dates.forEach(date => {
                const dayLabel = document.createElement('div');
                dayLabel.className = 'day-label';
                dayLabel.textContent = date.toLocaleDateString('en-US', { weekday: 'short' });
                grid.appendChild(dayLabel);
            });

            // Create time labels and cells
            for (let hour = 0; hour < 24; hour++) {
                // Time label
                const timeLabel = document.createElement('div');
                timeLabel.className = 'time-label';
                const time = new Date();
                time.setHours(hour, 0, 0, 0);
                timeLabel.textContent = formatTime(time);
                grid.appendChild(timeLabel);

                // Event cells for each day
                dates.forEach(date => {
                    const cell = document.createElement('div');
                    cell.className = 'cell';
                    
                    // Create date in IST
                    const cellDate = new Date(date);
                    cellDate.setHours(hour, 0, 0, 0);
                    cell.dataset.date = cellDate.toISOString();
                    cell.dataset.hour = hour;
                    
                    // Add visual styling to cells
                    cell.style.border = '1px solid #ddd';
                    cell.style.minHeight = '60px';
                    cell.style.position = 'relative';
                    
                    cell.onclick = () => {
                        // Create dates in IST
                        const startDate = new Date(date);
                        startDate.setHours(hour, 0, 0, 0);
                        const endDate = new Date(date);
                        endDate.setHours(hour + 1, 0, 0, 0);
                        
                        // Clear any existing event selection
                        selectedEvent = null;
                        if (eventIdInput) eventIdInput.value = '';
                        
                        // Set form values
                        if (eventTitleInput) eventTitleInput.value = '';
                        if (eventDescriptionInput) eventDescriptionInput.value = '';
                        if (eventLocationInput) eventLocationInput.value = '';
                        if (eventStartInput) eventStartInput.value = startDate.toISOString().slice(0, 16);
                        if (eventEndInput) eventEndInput.value = endDate.toISOString().slice(0, 16);
                        
                        // Show form
                        if (deleteEventBtn) deleteEventBtn.style.display = 'none';
                        if (eventDetails) eventDetails.style.display = 'block';
                    };
                    
                    grid.appendChild(cell);
                });
            }
        }
    }

    function renderEvents(grid, dates) {
        if (!grid || !events) return;
        
        console.log('Rendering events for dates:', dates);
        console.log('Available events:', events);
        
        const cells = grid.querySelectorAll('.cell');
        cells.forEach(cell => {
            const cellDate = new Date(cell.dataset.date);
            const cellHour = parseInt(cell.dataset.hour) || 0;
            
            console.log('Processing cell:', {
                date: cellDate.toISOString(),
                hour: cellHour
            });
            
            events.forEach(event => {
                const eventStart = new Date(event.startAt);
                const eventEnd = new Date(event.endAt);
                
                console.log('Checking event:', {
                    title: event.title,
                    start: eventStart.toISOString(),
                    end: eventEnd.toISOString()
                });
                
                if (currentView === 'month') {
                    if (isSameDay(eventStart, cellDate)) {
                        console.log('Rendering event in month view:', event.title);
                        renderEvent(event, cell);
                    }
                } else {
                    // For week and day views, check if the event overlaps with this hour
                    if (isSameDay(eventStart, cellDate)) {
                        const eventStartHour = eventStart.getHours();
                        const eventEndHour = eventEnd.getHours();
                        
                        console.log('Event hours:', {
                            startHour: eventStartHour,
                            endHour: eventEndHour,
                            cellHour: cellHour
                        });
                        
                        // Check if the event overlaps with this hour
                        if ((cellHour >= eventStartHour && cellHour < eventEndHour) || 
                            (eventStartHour === cellHour)) {
                            console.log('Rendering event in week/day view:', event.title);
                            renderEvent(event, cell);
                        }
                    }
                }
            });
        });
    }

    function isSameDay(a, b) {
        return a.getFullYear() === b.getFullYear() &&
               a.getMonth() === b.getMonth() &&
               a.getDate() === b.getDate();
    }

    // Add date utility functions
    function toLocalDateTime(date) {
        return date.toLocaleString('en-US', {
            timeZone: 'Asia/Kolkata',
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        }).replace(/(\d+)\/(\d+)\/(\d+),\s(\d+):(\d+):(\d+)/, '$3-$1-$2T$4:$5:$6');
    }

    function fromLocalDateTime(dateStr) {
        const [datePart, timePart] = dateStr.split('T');
        const [year, month, day] = datePart.split('-');
        const [hour, minute] = timePart.split(':');
        return new Date(year, month - 1, day, hour, minute);
    }

    // Update loadEvents function
    async function loadEvents() {
        const start = new Date();
        start.setHours(0, 0, 0, 0);
        const end = new Date(start);
        end.setDate(end.getDate() + 7);

        const url = `${API_BASE}?start=${toLocalDateTime(start)}&end=${toLocalDateTime(end)}`;
        console.log('Fetching events from:', url);
        
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Failed to fetch events: ${response.status}`);
            }

            const data = await response.json();
            console.log('Fetched events:', data);
            
            if (Array.isArray(data)) {
                events = data.map(event => ({
                    ...event,
                    startAt: fromLocalDateTime(event.startAt),
                    endAt: fromLocalDateTime(event.endAt)
                }));
                renderCurrentView();
            } else {
                console.error('Invalid response format:', data);
                events = [];
                renderCurrentView();
            }
        } catch (error) {
            console.error('Error fetching events:', error);
            events = [];
            renderCurrentView();
            if (events.length > 0) {
                alert(error.message || 'Failed to load events. Please refresh the page.');
            }
        }
    }

    // Initialize calendar
    document.addEventListener('DOMContentLoaded', () => {
        console.log('Schedule.js loaded!');
        try {
            loadEvents();
        } catch (error) {
            console.error('Error initializing calendar:', error);
            alert('Failed to initialize calendar. Please refresh the page.');
        }
    });
});