"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import { getAccessToken, getUserInfo } from "@/lib/api/auth";

export default function MyPageEdit() {
  const router = useRouter();
  const [nickname, setNickname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [profileImage, setProfileImage] = useState("");
  const [previewImage, setPreviewImage] = useState("/default-profile.png");

  useEffect(() => {
    const token = getAccessToken();
    const { userUUID } = getUserInfo();

    axios
      .get(`http://35.203.149.35:8080/api/auth/users/${userUUID}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setNickname(res.data.data.nickname);
        setEmail(res.data.data.email);
        setProfileImage(res.data.data.profileImage);
        setPreviewImage(res.data.data.profileImage || "/default-profile.png");
      })
      .catch(() => {
        alert("❌ 사용자 정보 불러오기 실패");
      });
  }, []);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreviewImage(reader.result as string);
      setProfileImage(reader.result as string);
    };
    reader.readAsDataURL(file);
  };

  const handleSave = async () => {
    if (!nickname.trim()) {
      alert("닉네임을 입력해주세요.");
      return;
    }
    if (!email.trim()) {
      alert("이메일을 입력해주세요.");
      return;
    }

    const token = getAccessToken();
    const { userUUID } = getUserInfo();

    try {
      await axios.put(
        `http://35.203.149.35:8080/api/auth/users/${userUUID}`,
        {
          nickname,
          email,
          password: password || undefined,
          profileImage,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      alert("✅ 저장되었습니다.");
      router.push("/mypage");
    } catch (err: any) {
      console.error("❌ 수정 실패:", err);
      alert("❌ 수정 실패: " + (err.response?.data?.msg || err.message));
    }
  };

  return (
    <div className="max-w-xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">프로필 수정</h2>
      <div className="flex flex-col items-center gap-4 mb-6">
        <img
          src={previewImage}
          alt="프로필"
          className="w-24 h-24 rounded-full object-cover border"
        />
        <label className="px-4 py-2 bg-gray-200 rounded cursor-pointer hover:bg-gray-300">
          이미지 변경
          <input
            type="file"
            accept="image/*"
            hidden
            onChange={handleImageChange}
          />
        </label>
      </div>

      <label className="block mb-2 font-medium">닉네임</label>
      <input
        type="text"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
        className="border px-4 py-2 rounded w-full mb-4"
      />

      <label className="block mb-2 font-medium">이메일</label>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        className="border px-4 py-2 rounded w-full mb-4"
      />

      <div className="flex justify-end gap-2">
        <button
          onClick={() => router.back()}
          className="px-4 py-2 bg-gray-100 rounded hover:bg-gray-200"
        >
          취소
        </button>
        <button
          onClick={handleSave}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          저장
        </button>
      </div>
    </div>
  );
}
