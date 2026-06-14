(function () {
    "use strict";

    const refreshMs = 30000;
    const storageKey = "predictor.scroll.position";

    const path = window.location.pathname;

    const shouldRefresh =
        path.includes("/tournaments/") ||
        path.includes("/leagues/");

    if (!shouldRefresh) {
        return;
    }

    function getScrollContainer() {
        return document.querySelector(".tournament-body")
            || document.querySelector(".main-body")
            || document.documentElement;
    }

    function restoreScroll() {
        const saved = sessionStorage.getItem(storageKey);

        if (!saved) {
            return;
        }

        try {
            const state = JSON.parse(saved);

            if (state.path !== path) {
                return;
            }

            const container = getScrollContainer();

            setTimeout(function () {
                container.scrollTop = state.scrollTop || 0;
                container.scrollLeft = state.scrollLeft || 0;
            }, 80);

        } catch (e) {
            sessionStorage.removeItem(storageKey);
        }
    }

    function saveScroll() {
        const container = getScrollContainer();

        sessionStorage.setItem(storageKey, JSON.stringify({
            path: path,
            scrollTop: container.scrollTop || 0,
            scrollLeft: container.scrollLeft || 0
        }));
    }

    restoreScroll();

    setTimeout(function () {
        saveScroll();
        window.location.reload();
    }, refreshMs);

})();