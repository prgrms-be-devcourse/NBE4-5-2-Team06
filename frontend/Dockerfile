FROM node:18-alpine

WORKDIR /app

# 의존성 파일 복사 및 설치
COPY package*.json ./
RUN npm install
RUN npm install axios

# ESLint 설정 파일 생성
RUN echo '{ "extends": "next/core-web-vitals", "rules": { "no-explicit-any": "off", "no-unused-vars": "off" } }' > .eslintrc.json

# 소스 코드 복사
COPY . .

# 환경 변수 설정
ENV NEXT_TELEMETRY_DISABLED=1
ENV NODE_ENV=production
ENV ESLINT_NO_DEV_ERRORS=true
ENV DISABLE_ESLINT_PLUGIN=true
ENV NEXT_DISABLE_ESLINT=1

# 빌드 시 린트 검사 비활성화
RUN npm run build --no-lint || npm run build

EXPOSE 3000

CMD ["npm", "start"]