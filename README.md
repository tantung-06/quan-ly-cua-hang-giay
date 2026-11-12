# Hệ thống quản lý cửa hàng giày

Ứng dụng desktop Java Swing quản lý cửa hàng giày, gồm đăng nhập, trang chủ,
thống kê doanh thu/tồn kho và các danh mục sản phẩm, khách hàng, nhân viên, nhà
cung cấp, phiếu nhập, phiếu xuất, tài khoản, phân quyền và khuyến mãi.

## Chạy ứng dụng

1. Với cài đặt mới, tạo cơ sở dữ liệu bằng script `database/quanlycuahanggiay.sql`.
   Với database đã có dữ liệu, thực hiện migration điểm tích lũy ở phần dưới;
   không chạy lại script tạo database lên dữ liệu đang sử dụng.
2. Cập nhật URL, tên đăng nhập và mật khẩu MySQL trong
   `src/config/DatabaseConnection.java`.
3. Thêm MySQL Connector/J vào classpath khi biên dịch và chạy.
4. Từ thư mục dự án, biên dịch và chạy bằng PowerShell:

```powershell
$sources = Get-ChildItem src -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $sources
java -cp "out;lib\mysql-connector-j.jar" App
```

Đăng nhập bằng tài khoản đang hoạt động trong bảng `TaiKhoan`. Các màn hình danh
mục hỗ trợ tìm kiếm, làm mới, thêm, sửa và xóa theo nghiệp vụ; màn hình phiếu
nhập và phiếu xuất hỗ trợ lập phiếu, hủy phiếu đang chờ xử lý, xác nhận nhập
kho hoặc hoàn tất xuất kho. Thao tác xác nhận cập nhật trạng thái và tồn kho trong cùng giao dịch;
phiếu xuất chỉ hoàn tất khi kho đủ số lượng. Dữ liệu được đọc và ghi qua các lớp
BUS hiện có trong dự án.

Ảnh trang chủ đặt tại `src/imgs/trang-chu.png`, tự co giãn giữ tỷ lệ để phủ kín vùng nội dung; phần thừa được cắt đều ở hai phía. Trang chủ không hiển thị tiêu đề hay lời chào.

Trong **Quản lý tài khoản**, tài khoản đăng nhập thuộc nhóm **Quản trị viên
(`NQ001`)** sẽ thấy thêm cột **Mật khẩu**, nằm sau cột **Tên đăng nhập**. Các nhóm
quyền khác giữ bảng bốn cột và không hiển thị mật khẩu.

## Cấu trúc mã nguồn

- `src/gui/MainFrame.java`: khung cửa sổ, menu và điều hướng.
- `src/gui/module`: cấu hình cột dữ liệu và thao tác của từng module.
- `src/gui/panel/DataPage.java`: bảng danh sách dùng chung, tìm kiếm, lọc trạng thái
  và xử lý thao tác nền. Các panel danh mục kế thừa cùng thành phần này.
- `src/gui/component`: nút điều hướng và icon vector.
- `src/gui/dialog`: biểu mẫu phiếu nhập/phiếu xuất và chi tiết; `PhieuLineTableModel` quản lý số
  lượng, đơn giá và tổng tiền của các dòng.
- `src/bus`, `src/dao`, `src/dto`: kiểm tra nghiệp vụ, truy cập SQL và đối tượng dữ liệu.
- `src/config/DatabaseConnection.java`: tạo kết nối riêng cho mỗi thao tác DAO;
  kết nối, statement và result set được đóng bằng `try-with-resources`.

Thư mục `src/config` chỉ chứa `DatabaseConnection.java` và `TestConnection.java`.
Interface tạo kết nối nằm trong `DatabaseConnection`; tiện ích cập nhật cấu trúc
database nằm tại `src/dao/MigrateDiemTichLuy.java`.

MySQL Connector/J được JDBC tự động nạp khi JAR có trong classpath. Khuyến mãi dùng
các giá trị `GIAM_TI_LE` / `GIAM_TIEN` và `AP_DUNG` / `HET_HAN`, thống nhất với SQL.
Khi lập giao dịch, các dòng cùng sản phẩm và đơn giá được gộp số lượng. Muốn đổi đơn
giá nhập của sản phẩm đã có trong phiếu nhập, bỏ dòng cũ rồi thêm lại.

## Áp dụng khuyến mãi cho phiếu xuất

Trong màn hình **Phiếu xuất → Lập phiếu xuất**, chọn khách hàng, thêm sản phẩm rồi
chọn khuyến mãi ở phần dưới danh sách. Có thể giữ **Không áp dụng khuyến mãi** để
bán theo giá gốc. Khách lẻ cần được đăng ký/chọn thành khách hàng trước khi dùng mã.
Danh sách chỉ hiện chương trình đang áp dụng, còn hiệu lực và khách chưa sử dụng.

Mỗi phiếu xuất chỉ áp dụng một mã; mỗi khách hàng chỉ dùng mỗi mã một lần. Lượt dùng
được ghi nhận khi hoàn tất xuất kho. Phiếu xuất chờ hoặc xử lý thất bại chưa
tiêu lượt dùng. Phiếu đã xuất không được hủy. Đổi khách hàng
sẽ xóa mã đang chọn và tải lại danh sách phù hợp. Hệ thống kiểm tra khi lập phiếu xuất
và kiểm tra lại trong transaction hoàn tất, nên hai phiếu đồng thời không
thể cùng dùng một mã cho cùng khách hàng. Các phiếu xuất cũ đã xuất cũng được tính
là đã sử dụng; không cần thêm bảng hoặc chạy migration mới.

Phiếu xuất hiển thị riêng **Tiền hàng**, **Giảm giá** và **Cần thanh toán**. Chương trình
giảm theo phần trăm hoặc số tiền cố định được áp dụng khi tiền hàng đạt đơn tối
thiểu; tiền giảm tối đa bằng tiền hàng. Nếu bỏ sản phẩm làm đơn hàng không còn đủ
điều kiện, cần bổ sung hàng hoặc bỏ chọn khuyến mãi trước khi lập phiếu.

Khi lưu, hệ thống đọc lại khuyến mãi để kiểm tra điều kiện và mức giảm. Nếu ưu đãi
đã thay đổi hoặc hết hiệu lực, phiếu xuất bị từ chối và hiển thị lý do. Mã khuyến mãi,
tổng thanh toán và các dòng sản phẩm được lưu trong cùng giao dịch. Chi tiết
sản phẩm giữ nguyên đơn giá; phần **Xem phiếu xuất** hiển thị mã ưu đãi và số tiền đã
giảm từ dữ liệu phiếu đã lưu.

## Điểm tích lũy

- Mỗi 10.000 VNĐ **thực trả** được cộng 1 điểm, lấy phần nguyên. Mỗi điểm sử dụng
  giảm 1.000 VNĐ. Khuyến mãi được tính trước, điểm sử dụng được trừ sau; chỉ phần
  tiền còn phải thanh toán mới được dùng để tính điểm thưởng.
- Chọn khách hàng khi lập phiếu xuất để xem điểm hiện có, nhập điểm sử dụng và
  xem điểm thưởng dự kiến. Khách lẻ không dùng hoặc tích điểm. Không thể dùng quá
  số điểm hiện có, vượt tiền sau khuyến mãi hoặc dùng điểm sẽ được cộng từ chính đơn đó.
- Nút **Hoàn tất xuất kho** yêu cầu nhân viên xác nhận đã giao hàng. Trạng thái
  `DA_XUAT` là phiếu xuất đã hoàn tất. Cập nhật tồn kho, trừ/cộng
  điểm, lịch sử và trạng thái phiếu nằm trong cùng transaction. Phiếu chờ chưa
  trừ điểm và chưa giữ chỗ điểm; số dư được khóa và kiểm tra lại khi thanh toán.
- Ví dụ: tiền hàng 600.000, giảm khuyến mãi 10%, dùng 20 điểm → thực trả 520.000,
  được cộng 52 điểm sau khi thanh toán thành công.
- Chỉ phiếu xuất đang chờ xử lý mới được hủy; hủy phiếu này không
  thay đổi điểm hoặc tồn kho. Phiếu xuất đã hoàn tất không được hủy, kể cả
  khi gọi trực tiếp BUS/DAO. Xác nhận đồng thời vẫn được bảo vệ bằng khóa phiếu
  trong transaction để tránh vừa xuất kho vừa hủy cùng một phiếu.
- Số dư điểm luôn không âm. Điểm được cộng sau thanh toán có thể sử dụng ngay
  cho các lần mua tiếp theo.
- Vào **Khách hàng → chọn khách → Lịch sử điểm** để xem cộng, sử dụng và số dư
  sau từng giao dịch. Các bản ghi hoàn/thu hồi từ phiên bản cũ được giữ lại để
  đối chiếu. Trường điểm ở biểu mẫu khách hàng chỉ đọc;
  chỉnh sửa thông tin liên hệ không được ghi đè số dư điểm.

Các lớp chính: `dto/SoDuDiem`, `dto/LichSuDiem`, `dao/LichSuDiemDAO`,
`bus/DiemTichLuyBUS`. `KhachHang` giữ số dư; `PhieuXuat` lưu điểm sử dụng,
điểm thực tế được cộng và cờ đã xử lý điểm. DAO dùng cùng connection của transaction;
thứ tự khóa là phiếu xuất → khách hàng → sản phẩm theo mã.

### Cập nhật database cũ

Dừng ứng dụng, biên dịch mã nguồn rồi chạy:

```powershell
java -cp 'out;lib/mysql-connector-j.jar' dao.MigrateDiemTichLuy
```

Thư mục `database` chỉ có một file: `quanlycuahanggiay.sql`. Lệnh Java đọc định
nghĩa bảng và câu lệnh số dư đầu kỳ từ chính file này để bổ sung phần còn thiếu
cho database cũ, không chạy phần tạo database hoặc dữ liệu mẫu.

Lệnh giữ nguyên số dư khách hàng và số tiền/trạng thái của các phiếu cũ. Các đơn
cũ không được cộng lại hoặc bị suy diễn điểm thưởng khi hủy. Bản đối chiếu được
ghi vào `out/loyalty-before-*.txt`; chạy lại không ghi trùng lịch sử. Lệnh cũng
kiểm tra bảng lịch sử hiện hữu và có thể tiếp tục một migration dở dang. Với phiên
bản cũ, lệnh loại bỏ cột `diemNo` ở khách hàng và `noSau` trong lịch sử, lưu giá trị
cũ vào bản đối chiếu trước khi bỏ cột; số dư và các bản ghi lịch sử được giữ nguyên.
Cài đặt mới chạy trực tiếp `database/quanlycuahanggiay.sql`, không cần chạy thêm
lệnh migration.

## Kiểm tra hồi quy

Sau khi biên dịch mã nguồn theo hướng dẫn trên, chạy bằng PowerShell:

```powershell
$testSources = Get-ChildItem tests -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp out -d out/tests $testSources
java '-Djava.awt.headless=true' -cp 'out/tests;out' RegressionTests
```

Bộ kiểm tra bao gồm kiểm tra dữ liệu đầu vào, tính tiền và điểm, tải/lọc danh sách
Swing, cấu hình module, tham số SQL, ánh xạ dữ liệu và quyền sở hữu kết nối JDBC.
JDBC được giả lập trong bộ kiểm tra này, không cần MySQL hoặc thư viện kiểm thử bổ sung.

Để kiểm tra transaction trên MySQL thật, sau khi biên dịch các bộ kiểm tra:

```powershell
java -cp 'out/tests;out;lib/mysql-connector-j.jar' MySqlLoyaltyIntegrationTests
```

Bộ kiểm tra này cần tài khoản MySQL có quyền tạo database. Nó tạo một database
riêng có tiền tố `qlgiay_loyalty_test_`, kiểm tra khuyến mãi kết hợp điểm, chặn hủy
đơn đã xuất, rollback và xác nhận đồng thời;
sau đó chỉ xóa database thử nghiệm do chính nó tạo. Dữ liệu ứng dụng không được
dùng làm dữ liệu thử nghiệm.
