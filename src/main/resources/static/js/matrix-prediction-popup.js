(function () {
    "use strict";

    function initMatrixPredictionPopup() {
        const popup = document.getElementById("matrixPredictionPopup");
        const rows = document.querySelectorAll(".matrix-match-row");

        console.log("[MATRIX POPUP] init", {
            popup: !!popup,
            rows: rows.length
        });

        if (!popup || rows.length === 0) {
            return;
        }

        function buildSummary(cells) {

            const counts = new Map();

            cells.forEach(cell => {
                const pred = (cell.dataset.prediction || "-").trim();
                counts.set(pred, (counts.get(pred) || 0) + 1);
            });

            return Array.from(counts.entries())
                .sort((a, b) => b[1] - a[1] || a[0].localeCompare(b[0]))
                .map(([pred, count]) => `
                    <div class="matrix-summary-row">
                        <span class="matrix-summary-count">${count}</span>
                        <span class="matrix-summary-x">x</span>
                        <span class="matrix-summary-pred">${pred}</span>
                    </div>
                `)
                .join("");
        }

        function showPopup(row, event) {
            const title = row.dataset.matchTitle || "Match";
            const no = row.dataset.matchNo || "";
            const official = row.dataset.official || "-";
            const cells = Array.from(row.querySelectorAll(".mx-player-cell"));

            const summary = buildSummary(cells);

            popup.innerHTML = `
                <div class="matrix-popup-title">Match ${no}: ${title}</div>
                <div class="matrix-popup-official">Official: ${official}</div>

                <div class="matrix-popup-summary">
                    ${summary}
                </div>

                <div class="matrix-popup-divider"></div>

                <div class="matrix-popup-players">
                    ${cells.map(cell => {
                        const player = cell.dataset.player || "Player";
                        const pred = cell.dataset.prediction || "-";
                        const points = cell.dataset.points || "";
                        const css = cell.dataset.css || "";

                        return `
                            <div class="matrix-popup-player-row ${css}">
                                <span>${player}</span>
                                <strong>${pred}</strong>
                                <em>${points ? points + "p" : ""}</em>
                            </div>
                        `;
                    }).join("")}
                </div>
            `;

            popup.hidden = false;

            const clientX = event.clientX || 40;
            const clientY = event.clientY || 120;

            const x = Math.min(clientX + 14, window.innerWidth - 330);
            const y = Math.min(clientY + 14, window.innerHeight - 420);

            popup.style.left = Math.max(8, x) + "px";
            popup.style.top = Math.max(8, y) + "px";
        }

        function hidePopup() {
            popup.hidden = true;
        }

        rows.forEach(row => {
            row.addEventListener("mouseenter", function (event) {
                showPopup(row, event);
            });

            row.addEventListener("mousemove", function (event) {
                if (!popup.hidden) {
                    showPopup(row, event);
                }
            });

            row.addEventListener("mouseleave", hidePopup);

            row.addEventListener("click", function (event) {
                event.stopPropagation();
                showPopup(row, event);
            });

            row.addEventListener("touchstart", function (event) {
                event.stopPropagation();

                const touch = event.touches && event.touches.length > 0
                    ? event.touches[0]
                    : null;

                showPopup(row, touch || event);
            }, { passive: true });
        });

        document.addEventListener("click", hidePopup);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initMatrixPredictionPopup);
    } else {
        initMatrixPredictionPopup();
    }

})();