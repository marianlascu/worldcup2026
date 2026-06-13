(function () {
    "use strict";

    const refreshMs = 30000;

    const path = window.location.pathname;

    const shouldRefresh =
        path.includes("/tournaments/") ||
        path.includes("/leagues/");

    if (!shouldRefresh) {
        return;
    }

    setTimeout(function () {
        window.location.reload();
    }, refreshMs);
})();