/** Math utilities used across the codebase. */
export class MathUtils {
    static clamp(value: number, min: number, max: number): number {
        return Math.max(min, Math.min(max, value));
    }

    /** Returns how much to subtract given current progress toward max. */
    static linearDecay(current: number, max: number, maxPenalty: number): number {
        if (max <= 0) return 0;
        return MathUtils.clamp((current / max) * maxPenalty, 0, maxPenalty);
    }

    /** Memoised recursive Fibonacci. */
    static fibonacci(n: number): number {
        const memo = new Map<number, number>();
        function fib(k: number): number {
            if (k <= 1) return k;
            if (memo.has(k)) return memo.get(k)!;
            const r = fib(k - 1) + fib(k - 2);
            memo.set(k, r);
            return r;
        }
        return fib(n);
    }

    /** Population standard deviation. */
    static standardDeviation(values: number[]): number {
        if (!values.length) return 0;
        const mean     = values.reduce((s, v) => s + v, 0) / values.length;
        const variance = values.reduce((s, v) => s + (v - mean) ** 2, 0) / values.length;
        return Math.sqrt(variance);
    }

    /** Iterative factorial. */
    static factorial(n: number): number {
        if (n < 0) throw new Error('Negative input to factorial');
        let result = 1;
        for (let i = 2; i <= n; i++) result *= i;
        return result;
    }
}
