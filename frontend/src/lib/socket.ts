import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient: Client | null = null;

/**
 * 소켓 연결 함수 (토큰 포함)
 * @param token JWT 토큰
 * @returns STOMP Client
 */
export const connectStomp = (token: string): Client => {
  stompClient = new Client({
    webSocketFactory: () => {
      // SockJS 연결, 쿼리 파라미터로 토큰 전달 (핸드쉐이크)
      return new SockJS(`http://35.203.149.35:8080/ws?token=${token}`);
    },
    connectHeaders: {
      Authorization: `Bearer ${token}`, // STOMP 연결 시 헤더에 토큰 추가
    },
    debug: (str) => console.log("[STOMP DEBUG]", str),
    reconnectDelay: 5000, // 재연결 주기 (5초)
  });

  console.log("[socket.ts] STOMP 클라이언트 연결 시도");
  stompClient.activate(); // 연결 시작
  return stompClient;
};

/**
 * 경매 소켓 구독 함수
 * @param client STOMP Client
 * @param auctionId 경매 ID
 * @param onMessage 메시지 수신 시 콜백
 */
export const subscribeToAuction = (
  client: Client,
  auctionId: string,
  onMessage: (message: any) => void
) => {
  console.log("[socket.ts] 경매 구독 시작. 경매 ID:", auctionId);

  client.onConnect = () => {
    console.log("[socket.ts] STOMP 연결 성공. 구독 진행");
    client.subscribe(`/sub/auction/${auctionId}`, (msg) => {
      console.log("[socket.ts] STOMP 메시지 수신:", msg.body);
      onMessage(JSON.parse(msg.body));
    });
  };
};

/**
 * 경매 입찰, 채팅 등 메시지 전송 함수 (Body에 토큰 포함)
 * @param destination 서버로 보낼 목적지 (ex: "/app/auction/bid")
 * @param message 보낼 메시지 (객체)
 * @param token JWT 토큰 (Body로 포함)
 */
export const sendAuctionMessage = (
  destination: string,
  message: any,
  token: string
) => {
  if (!stompClient || !stompClient.connected) {
    console.error("[socket.ts] STOMP 연결 안 됨. 메시지 전송 실패.");
    return;
  }

  console.log(`[socket.ts] 메시지 전송: ${destination}`, message);

  // 토큰을 body에 포함
  const messageWithToken = {
    ...message,
    token: token, // body 안에 token 필드로 포함
  };

  stompClient.publish({
    destination: destination, // 예: "/app/auction/bid"
    body: JSON.stringify(messageWithToken), // JSON 문자열로 변환
    headers: {}, // 헤더 비워둠 (SockJS는 헤더 깨질 수 있음)
  });
};

/**
 * 소켓 연결 해제 함수
 */
export const disconnectStomp = () => {
  if (stompClient) {
    stompClient.deactivate(); // 연결 해제
    console.log("[socket.ts] STOMP 클라이언트 연결 해제");
  }
};
