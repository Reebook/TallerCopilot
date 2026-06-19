FROM node:24-alpine AS build
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN node node_modules/.bin/tsc -b && node node_modules/.bin/vite build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 3000
