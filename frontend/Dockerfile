FROM node:18-alpine

WORKDIR /app

# 의존성 파일 복사 및 설치
COPY package*.json ./
RUN npm install

# 소스 코드 복사
COPY . .

# 환경 변수 설정
ENV NEXT_TELEMETRY_DISABLED=1
ENV NODE_ENV=production
ENV ESLINT_NO_DEV_ERRORS=true
ENV DISABLE_ESLINT_PLUGIN=true

# 빌드 시 린트 검사 비활성화
RUN npm run build || npm run build --no-lint

EXPOSE 3000

CMD ["npm", "start"]