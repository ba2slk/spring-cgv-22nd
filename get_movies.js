import http from "k6/http";
import { sleep } from "k6";

export const options = {
    vus: 1000, // 가상 사용자 수
    duration: "10s", // 테스트 시간
};

export default function () {
    http.get("http://localhost:8080/api/movies");
    sleep(1);
}