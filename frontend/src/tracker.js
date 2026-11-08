const ENDPOINT = 'https://script.google.com/macros/s/AKfycbwO880vNoSaOKTCM_gyY9JKqjKFOeAz6kZjoG0NdcAum40l15tvfl0XrcCpaYiqdEe1/exec';
const VISITOR_KEY = 'vl_visitor_id';
const SESSION_KEY = 'vl_session';

let session;

function randomVisitorId() {
    return 'VIS-' + Math.floor(100000 + Math.random() * 900000);
}

function visitorId() {
    try {
        let id = localStorage.getItem(VISITOR_KEY);
        if (!id) {
            id = randomVisitorId();
            localStorage.setItem(VISITOR_KEY, id);
        }
        return id;
    } catch {
        return randomVisitorId();
    }
}

function device() {
    const ua = navigator.userAgent;
    if (/iPad|Tablet|PlayBook|Silk|(Android(?!.*Mobile))/i.test(ua)) return 'Tablet';
    if (/Mobi|iPhone|iPod|Android/i.test(ua)) return 'Mobile';
    return 'Desktop';
}

function trafficSource() {
    const p = new URLSearchParams(location.search);
    const medium = (p.get('utm_medium') || '').toLowerCase();
    if (p.get('gclid') || /^(cpc|ppc|paid)/.test(medium)) return 'Paid Search';
    if (medium === 'social') return 'Social';

    let host = '';
    try { host = new URL(document.referrer).hostname; } catch {}
    if (!host || host === location.hostname) return 'Direct';
    if (/(^|\.)(google|bing|duckduckgo|yahoo|baidu|ecosia)\./.test(host)) return 'Organic Search';
    if (/(^|\.)(facebook\.com|instagram\.com|linkedin\.com|lnkd\.in|twitter\.com|x\.com|t\.co|reddit\.com|youtube\.com|tiktok\.com|pinterest\.com)$/.test(host)) return 'Social';
    return 'Referral';
}

function loadSession() {
    try {
        const saved = sessionStorage.getItem(SESSION_KEY);
        if (saved) return JSON.parse(saved);
    } catch {}
    return {
        start: Date.now(),
        landing: location.pathname,
        source: trafficSource(),
        country: 'Unknown',
        pages: 0,
        converted: false,
        sent: false,
    };
}

function save() {
    try { sessionStorage.setItem(SESSION_KEY, JSON.stringify(session)); } catch {}
}

function lookupCountry() {
    if (session.country !== 'Unknown') return;
    fetch('https://ipapi.co/country_name/')
        .then((r) => (r.ok ? r.text() : 'Unknown'))
        .then((name) => { session.country = name.trim() || 'Unknown'; save(); })
        .catch(() => {});
}

function flush() {
    if (session.sent || session.pages === 0) return;
    session.sent = true;
    save();

    const body = JSON.stringify({
        visitorId: visitorId(),
        device: device(),
        source: session.source,
        landing: session.landing,
        pages: session.pages,
        duration: Math.round((Date.now() - session.start) / 1000),
        country: session.country,
        bounce: session.pages <= 1 ? 'Yes' : 'No',
        converted: session.converted ? 'Yes' : 'No',
    });

    // text/plain avoids a CORS preflight, which Apps Script doesn't support
    const blob = new Blob([body], { type: 'text/plain;charset=UTF-8' });
    if (!navigator.sendBeacon || !navigator.sendBeacon(ENDPOINT, blob)) {
        fetch(ENDPOINT, { method: 'POST', body, mode: 'no-cors', keepalive: true });
    }
}

export function trackPageView() {
    if (!session) return;
    session.pages += 1;
    save();
}

export function trackConversion() {
    if (!session) return;
    session.converted = true;
    save();
}

export function initTracking() {
    if (session) return;
    if (location.hostname === 'localhost') return; // keep dev traffic out of the sheet
    session = loadSession();
    lookupCountry();
    trackPageView();
    window.addEventListener('pagehide', flush);
    document.addEventListener('visibilitychange', () => {
        if (document.visibilityState === 'hidden') flush();
    });
}