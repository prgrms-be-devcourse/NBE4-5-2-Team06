FROM node:18-alpine

WORKDIR /app

# 의존성 파일 복사
COPY package*.json ./

# 필요한 패키지 명시적 설치 (axios 추가)
RUN npm install
RUN npm install axios

# 소스 코드 복사
COPY . .

# ESLint 검사 건너뛰기
ENV NEXT_TELEMETRY_DISABLED 1
ENV ESLINT_NO_DEV_ERRORS true
ENV NODE_ENV production

# 빌드
RUN npm run build --no-lint

EXPOSE 3000

CMD ["npm", "start"]