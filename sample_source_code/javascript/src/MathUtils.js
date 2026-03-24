/**
 * Math utilities used across modules.
 * Demonstrates complex standalone helper functions.
 */
const MathUtils = {
    /**
     * Clamps value to [min, max].
     * @param {number} value @param {number} min @param {number} max
     */
    clamp(value, min, max) {
        return Math.max(min, Math.min(max, value));
    },

    /**
     * Returns how much to subtract given current progress toward max.
     * @param {number} current @param {number} max @param {number} maxPenalty
     */
    linearDecay(current, max, maxPenalty) {
        if (max <= 0) return 0;
        return this.clamp((current / max) * maxPenalty, 0, maxPenalty);
    },

    /**
     * Memoised recursive Fibonacci.
     * @param {number} n
     * @returns {number}
     */
    fibonacci(n) {
        const memo = new Map();
        function fib(k) {
            if (k <= 1) return k;
            if (memo.has(k)) return memo.get(k);
            const result = fib(k - 1) + fib(k - 2);
            memo.set(k, result);
            return result;
        }
        return fib(n);
    },

    /**
     * Population standard deviation.
     * @param {number[]} values
     */
    standardDeviation(values) {
        if (!values.length) return 0;
        const mean     = values.reduce((s, v) => s + v, 0) / values.length;
        const variance = values.reduce((s, v) => s + (v - mean) ** 2, 0) / values.length;
        return Math.sqrt(variance);
    },

    /**
     * Iterative factorial.
     * @param {number} n
     */
    factorial(n) {
        if (n < 0) throw new Error('Negative input to factorial');
        let result = 1;
        for (let i = 2; i <= n; i++) result *= i;
        return result;
    },
};

module.exports = MathUtils;
