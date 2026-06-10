(function () {
    function pad(n) {
        return String(n).padStart(2, "0");
    }

    function formatRemaining(ms) {
        if (ms <= 0) {
            return "LIVE";
        }

        const totalSeconds = Math.floor(ms / 1000);
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;

        return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
    }

    function updateCountdowns() {
        const nodes = Array.from(document.querySelectorAll(".kickoff-countdown"));
        const now = Date.now();

        let nextNode = null;
        let nextTime = null;

        nodes.forEach(node => {
            const raw = node.dataset.kickoff;

            if (!raw) {
                node.textContent = "-";
                node.classList.remove("is-live");
                return;
            }

            const t = new Date(raw).getTime();

            if (t > now && (nextTime === null || t < nextTime)) {
                nextTime = t;
                nextNode = node;
            }

            node.textContent = "-";
            node.classList.remove("is-live");
        });

        if (nextNode && nextTime) {
            const diff = nextTime - now;
            nextNode.textContent = formatRemaining(diff);
            nextNode.classList.remove("is-live");
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        updateCountdowns();
        setInterval(updateCountdowns, 1000);
    });
})();