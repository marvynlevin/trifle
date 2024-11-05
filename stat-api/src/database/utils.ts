export function formatDuration(nanoseconds: number): string {
    const hours = Math.floor(nanoseconds / (3600 * 1_000_000_000));
    const minutes = Math.floor((nanoseconds % (3600 * 1_000_000_000)) / (60 * 1_000_000_000));
    const seconds = Math.floor((nanoseconds % (60 * 1_000_000_000)) / (1_000_000_000));
    const milliseconds = Math.floor((nanoseconds % (1_000_000_000)) / 1_000_000);
    const remainingNanos = nanoseconds % 1_000_000;

    const parts: string[] = [];
    if (hours > 0) {
        parts.push(`${hours}h`);
    }
    if (minutes > 0) {
        parts.push(`${minutes}m`);
    }
    if (seconds > 0) {
        parts.push(`${seconds}s`);
    }
    if (milliseconds > 0 || remainingNanos > 0) {
        parts.push(`${milliseconds}${remainingNanos > 0 ? '.' + Math.floor(remainingNanos) : ''}ms`);
    }

    let parts_trimmed = parts.join(' ').trim();

    if (parts_trimmed.length < 1) return "0ms"
    else return parts_trimmed;
}
