export async function loginUser(email: string, password: string) {
  const response = await fetch(`http://35.203.149.35:8080/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const res = await response.json(); // 응답 데이터를 JSON으로 변환

  if (!response.ok) {
    throw new Error(res.msg);
  }

  return res.data;
}

export async function signupUser(
  email: string,
  password: string,
  nickname: string
) {
  const response = await fetch(`http://35.203.149.35:8080/api/auth/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password, nickname }),
  });

  const res = await response.json(); // 응답 데이터를 JSON으로 변환

  if (!response.ok) {
    throw new Error(res.msg);
  }

  return res.msg; // 성공 응답 반환 (userUUID 포함)
}

export const getAccessToken = () => {
  return localStorage.getItem("accessToken");
};

export const getUserInfo = () => {
  return {
    userUUID: localStorage.getItem("userUUID"),
    nickname: localStorage.getItem("nickname"),
  };
};

export const removeAuthData = () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userUUID");
  localStorage.removeItem("nickname");
};
