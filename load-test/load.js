import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 500,              // 동시 사용자 500명
    duration: '3m',        // 3분간 지속
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내
        http_req_failed: ['rate<0.1'],     // 실패율 10% 미만
    },
};

export default () => {
    const res = http.get('http://localhost:8080/api/v1/main');
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 2000ms': (r) => r.timings.duration < 2000,
    });
    sleep(0.1);            // 0.1초마다 호출 (초당 10회)
};