(function () {
    'use strict';

    var SVG_NS = 'http://www.w3.org/2000/svg';
    var root = document.getElementById('ucl-bracket');

    if (!root) {
        return;
    }

    var svg = document.createElementNS(SVG_NS, 'svg');
    svg.setAttribute('class', 'ucl-svg');
    svg.setAttribute('aria-hidden', 'true');
    root.insertBefore(svg, root.firstChild);

    function findTie(id) {
        return root.querySelector('[data-id="' + CSS.escape(id) + '"]');
    }

    function relativeBox(el) {
        var box = el.getBoundingClientRect();
        var base = root.getBoundingClientRect();

        return {
            left: box.left - base.left,
            right: box.right - base.left,
            top: box.top - base.top,
            bottom: box.bottom - base.top,
            midY: (box.top + box.bottom) / 2 - base.top,
            midX: (box.left + box.right) / 2 - base.left
        };
    }

    function drawLink(fromEl, toEl) {
        var from = relativeBox(fromEl);
        var to = relativeBox(toEl);
        var goingRight = from.midX < to.midX;

        var startX = goingRight ? from.right : from.left;
        var endX = goingRight ? to.left : to.right;
        var midX = (startX + endX) / 2;

        var path = document.createElementNS(SVG_NS, 'path');
        path.setAttribute('d', 'M ' + startX + ' ' + from.midY +
            ' H ' + midX + ' V ' + to.midY + ' H ' + endX);
        path.setAttribute('fill', 'none');
        path.setAttribute('stroke', 'rgba(255, 216, 107, 0.55)');
        path.setAttribute('stroke-width', '1.5');
        svg.appendChild(path);
    }

    function draw() {
        while (svg.firstChild) {
            svg.removeChild(svg.firstChild);
        }

        svg.setAttribute('width', root.clientWidth);
        svg.setAttribute('height', root.clientHeight);

        var sources = root.querySelectorAll('[data-next]');

        for (var i = 0; i < sources.length; i++) {
            var nextId = sources[i].getAttribute('data-next');
            var target = nextId ? findTie(nextId) : null;

            if (target) {
                drawLink(sources[i], target);
            }
        }
    }

    draw();

    window.addEventListener('load', draw);

    if (typeof ResizeObserver === 'function') {
        new ResizeObserver(draw).observe(root);
    } else {
        window.addEventListener('resize', draw);
    }
})();
