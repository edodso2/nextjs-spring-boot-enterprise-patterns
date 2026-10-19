import http from 'k6/http';
import { check } from 'k6';

const RATE = Number(__ENV.RATE || 20);
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  scenarios: {
    signups: {
      // Starts RATE new signups every second, however slow the responses are
      executor: 'constant-arrival-rate',
      rate: RATE,
      timeUnit: '1s',
      duration: __ENV.DURATION || '60s',
      preAllocatedVUs: RATE * 5,
      maxVUs: RATE * 20,
    },
  },
};

export default function () {
  // Unique email per request so signups don't collide with each other
  const id = `${Date.now()}-${__VU}-${__ITER}`;
  const res = http.post(
    `${BASE_URL}/api/users`,
    JSON.stringify({ email: `load-${id}@example.com`, firstName: 'Load', lastName: 'Test' }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  check(res, { 'status is 201': (r) => r.status === 201 });
}
