FROM node:18-alpine

WORKDIR /app

# 의존성 파일 복사
COPY package*.json ./

# 필요한 패키지 명시적 설치 (axios 추가)
RUN npm install
RUN npm install axios

# 소스 코드 복사
COPY . .

# 빌드
RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]