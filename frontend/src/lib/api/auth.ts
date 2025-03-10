export async function loginUser(email: string, password: string) {
  const response = await fetch(`http://localhost:8080/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const res = await response.json(); // 응답 데이터를 JSON으로 변환

  if (!response.ok) {
    throw new Error(res.msg);
  }

  // 백엔드 응답에서 토큰 추출
  const { token, userUuid, nickname } = res.data;

  // 토큰을 localStorage에 저장 (HttpOnly 쿠키는 JavaScript에서 설정 불가)
  localStorage.setItem("accessToken", token);
  localStorage.setItem("userUuid", userUuid);
  localStorage.setItem("nickname", nickname);

  return res.msg; // 로그인 성공 메시지 반환
}

  
  export async function signupUser(email: string, password: string, nickname: string) {
    const response = await fetch(`http://localhost:8080/api/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, nickname }),
    });

    const res = await response.json(); // 응답 데이터를 JSON으로 변환

    if (!response.ok) {
        throw new Error(res.msg);
    }

    return res.msg; // 성공 응답 반환 (user_uuid 포함)
}

export const getAccessToken = () => {
  return localStorage.getItem("accessToken");
};

export const getUserInfo = () => {
  return {
    userUuid: localStorage.getItem("userUuid"),
    nickname: localStorage.getItem("nickname"),
  };
};

export const removeAuthData = () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userUuid");
  localStorage.removeItem("nickname");
};
