# file: frontend/Dockerfile
FROM nginx:alpine
# Giả sử SPA được build ra thư mục dist/
COPY dist/ /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
