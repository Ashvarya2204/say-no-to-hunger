document.addEventListener("DOMContentLoaded", function () {

    // SAFE NAV TOGGLE
    const navLinks = document.querySelector('.nav-links');
    if (navLinks) {
        window.toggleMenu = function () {
            navLinks.classList.toggle('active');
        };
    }

    //SAFE TESTIMONIAL SLIDER 
    const testimonials = document.querySelectorAll('.testimonial');

    if (testimonials.length > 0) {
        let index = 0;

        setInterval(() => {
            testimonials[index].classList.remove('active');
            index = (index + 1) % testimonials.length;
            testimonials[index].classList.add('active');
        }, 3000);
    }

});
