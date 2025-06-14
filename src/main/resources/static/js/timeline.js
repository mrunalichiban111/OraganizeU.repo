document.addEventListener('DOMContentLoaded', function() {
    const timelineSection = document.getElementById('timeline');
    const timeblocks = document.getElementById('timeblocks');
    let isScrolling = false;
    let scrollTimeout;

    // Function to handle horizontal scroll animation
    function handleTimelineScroll() {
        if (!timelineSection || !timeblocks) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    // Timeline is in view, start horizontal scroll animation
                    animateTimelineScroll();
                }
            });
        }, {
            threshold: 0.2 // Trigger when 20% of the section is visible
        });

        observer.observe(timelineSection);
    }

    // Function to animate horizontal scroll
    function animateTimelineScroll() {
        if (isScrolling) return;
        isScrolling = true;

        const cards = timeblocks.querySelectorAll('.card');
        const totalWidth = timeblocks.scrollWidth - timeblocks.clientWidth;
        let currentScroll = 0;
        const scrollStep = totalWidth / (cards.length * 2); // Divide by 2 to make it smoother

        function scrollNext() {
            if (currentScroll >= totalWidth) {
                isScrolling = false;
                return;
            }

            currentScroll += scrollStep;
            timeblocks.scrollTo({
                left: currentScroll,
                behavior: 'smooth'
            });

            // Add animation class to the card that's currently in view
            cards.forEach(card => {
                const rect = card.getBoundingClientRect();
                if (rect.left >= 0 && rect.left <= window.innerWidth) {
                    card.classList.add('in-view');
                }
            });

            setTimeout(scrollNext, 1000); // Scroll every second
        }

        scrollNext();
    }

    // Initialize timeline scroll
    handleTimelineScroll();

    // Add scroll event listener for manual scroll
    timeblocks.addEventListener('scroll', () => {
        clearTimeout(scrollTimeout);
        scrollTimeout = setTimeout(() => {
            const cards = timeblocks.querySelectorAll('.card');
            cards.forEach(card => {
                const rect = card.getBoundingClientRect();
                if (rect.left >= 0 && rect.left <= window.innerWidth) {
                    card.classList.add('in-view');
                }
            });
        }, 100);
    });
}); 