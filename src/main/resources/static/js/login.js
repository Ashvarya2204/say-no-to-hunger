document.addEventListener("DOMContentLoaded", function () {

    const timerEl = document.getElementById("timer");

    if (timerEl) {
        let timeLeft = 600; // 10 minutes

        const timerInterval = setInterval(() => {

            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;

            timerEl.textContent =
                minutes + ":" + seconds.toString().padStart(2, '0');

            if (timeLeft <= 0) {
                timerEl.textContent = "EXPIRED";
                timerEl.style.color = "red";
                clearInterval(timerInterval);
            }

            timeLeft--;

        }, 1000);
    }

});
