(function () {
    "use strict";

    const refreshMs = 30000;

    function findRefreshTargets() {
        return document.querySelectorAll("[data-ajax-refresh-url]");
    }

    async function refreshTarget(target) {
        const url = target.dataset.ajaxRefreshUrl;

        if (!url) {
            return;
        }

        try {
            const response = await fetch(url, {
                method: "GET",
                cache: "no-store",
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                }
            });

            if (!response.ok) {
                console.warn("AJAX refresh failed:", url, response.status);
                return;
            }

            target.innerHTML = await response.text();

        } catch (e) {
            console.warn("AJAX refresh error:", url, e);
        }
    }

    function refreshAllTargets() {
        const targets = findRefreshTargets();

        if (!targets.length) {
            return;
        }

        targets.forEach(refreshTarget);
    }

    document.addEventListener("DOMContentLoaded", function () {
        if (!findRefreshTargets().length) {
            return;
        }

        setInterval(refreshAllTargets, refreshMs);
    });

})();