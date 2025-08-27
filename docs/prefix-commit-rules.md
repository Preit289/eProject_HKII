# Commit Message Prefix Rules

Áp dụng quy ước Conventional Commits để giữ lịch sử thay đổi rõ ràng, dễ đọc và thuận tiện tạo changelog tự động.

Cấu trúc cơ bản:
type(scope?): short description
- type: một trong các loại bên dưới
- scope (tùy chọn): phần của dự án (ví dụ: Booking, RoomMgmt, DB)
- short description: câu ngắn (imperative, không dấu chấm cuối)

Ví dụ: feat(Booking): add customer list page

Các loại commit phổ biến
- feat: Thêm tính năng mới  
  Ví dụ: feat(Booking): add customer list page

- fix: Sửa lỗi  
  Ví dụ: fix(DB): add status condition to SQL query for retrieving customer list

- docs: Cập nhật tài liệu (README, hướng dẫn, comment)  
  Ví dụ: docs: update API usage in README

- style: Thay đổi về định dạng, khoảng trắng, dấu chấm phẩy (không làm thay đổi logic)  
  Ví dụ: style: trim trailing whitespaces in README

- refactor: Tái cấu trúc code (không thêm tính năng, không sửa bug)  
  Ví dụ: refactor: simplify getCustomerList logic

- perf: Tối ưu hiệu năng  
  Ví dụ: perf: optimize SQL for customer list inquiry

- test: Thêm hoặc sửa test  
  Ví dụ: test: add unit tests for booking validation

- chore: Các công việc vặt liên quan tới toolchain, scripts, config (không ảnh hưởng sản phẩm)  
  Ví dụ: chore: update MySQL client version

- build: Thay đổi ảnh hưởng tới quá trình build hoặc artifact (Dockerfile, pom.xml, ...)  
  Ví dụ: build: update Dockerfile for CI build

- ci: Cập nhật cấu hình CI/CD (pipeline, workflow)  
  Ví dụ: ci: add new GitHub Actions workflow for tests

Một số lưu ý
- Viết ở dạng mệnh lệnh (imperative): "add", "fix", not "added"/"fixed".
- Giữ phần subject ngắn (≤ 72 ký tự) nếu có thể.
- Nếu cần mô tả chi tiết hơn, thêm body sau một dòng trống.
- Sử dụng scope để chỉ module/feature nhằm theo dõi dễ dàng.