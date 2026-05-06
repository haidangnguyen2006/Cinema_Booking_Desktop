# Class Diagram (Mermaid)

Dưới đây là sơ đồ lớp (class diagram) của dự án CinemaTicket thể hiện các lớp chính, mối quan hệ giữa DAO, Service, Model và View. Bạn có thể mở file này trong GitHub/GitLab để render Mermaid.

```mermaid
classDiagram
    direction LR

    %% Core: DB connection
    class DatabaseConnection {
      -String SERVER_NAME
      -String DATABASE_NAME
      -String USERNAME
      -String PASSWORD
      -String PORT
      +static Connection getConnection()
    }

    %% Models
    class Movie {
      -int movieId
      -String title
      -String posterUrl
      -String description
    }
    class ShowTime {
      -int showTimeId
      -int movieId
      -int roomId
      -Time startTime
      -double ticketPrice
    }
    class Seat {
      -int seatId
      -String seatName
      -boolean sold
    }
    class Room {
      -int roomId
      -String name
      -int capacity
    }
    class Customer {
      -int customerId
      -String fullName
      -String phone
      -int points
    }
    class Ticket {
      -int ticketId
      -int showTimeId
      -int seatId
      -double price
    }
    class Invoice {
      -int invoiceId
      -List~Ticket~ tickets
      -double total
    }
    class User {
      -int userId
      -String username
      -String password
      -Role role
    }

    %% DAOs
    class MovieDAO {
      +List~Movie~ getAllMovies()
      +Movie getMovieById(int id)
      +void insertMovie(Movie m)
      +void updateMovie(Movie m)
      +void deleteMovie(int id)
    }
    class ShowTimeDAO {
      +List~ShowTime~ getShowTimesByMovieAndDate(int movieId, Date date)
      +ShowTime getShowTimeById(int id)
    }
    class SeatDAO {
      +List~Seat~ getSeatsByRoom(int roomId)
      +void markSeatSold(int seatId)
    }
    class RoomDAO {
      +Room getRoomById(int id)
    }
    class CustomerDAO {
      +Customer findByPhone(String phone)
      +void insertCustomer(Customer c)
    }
    class InvoiceDAO {
      +void createInvoice(Invoice inv)
    }
    class UserDAO {
      +User findByUsername(String username)
    }
    class StatisticDAO {
      +Map~String,Object~ getMonthlyStats(int month, int year)
    }

    %% Services
    class BookingService {
      +boolean processPayment(ShowTime st, List~Seat~ seats, Customer c)
      +Invoice createInvoice(ShowTime st, List~Seat~ seats, Customer c)
    }
    class ShowTimeService {
      +List~ShowTime~ getShowTimesByMovieAndDate(int movieId, Date date)
      +List~Seat~ getSeatsForShowTime(int roomId)
    }
    class CustomerService {
      +Customer findCustomerByPhone(String phone)
      +Customer registerNewCustomer(String phone, String name)
    }
    class AuthService {
      +User authenticate(String username, String password)
    }
    class TMDBApiService {
      +List~Movie~ fetchNowPlaying()
      +Movie fetchMovieDetails(int movieId)
    }

    %% Utils & UI
    class ConfigLoader
    class SessionManager
    class LoginFrame
    class MainDashboardFrame
    class POSPanel
    class TicketDialog

    %% Relationships: DAOs depend on DatabaseConnection
    DatabaseConnection <|-- MovieDAO
    DatabaseConnection <|-- ShowTimeDAO
    DatabaseConnection <|-- SeatDAO
    DatabaseConnection <|-- RoomDAO
    DatabaseConnection <|-- CustomerDAO
    DatabaseConnection <|-- InvoiceDAO
    DatabaseConnection <|-- UserDAO
    DatabaseConnection <|-- StatisticDAO

    %% DAOs -> Models
    MovieDAO --> Movie
    ShowTimeDAO --> ShowTime
    SeatDAO --> Seat
    RoomDAO --> Room
    CustomerDAO --> Customer
    InvoiceDAO --> Invoice
    UserDAO --> User

    %% Services -> DAOs
    ShowTimeService --> ShowTimeDAO
    ShowTimeService --> SeatDAO
    BookingService --> ShowTimeDAO
    BookingService --> SeatDAO
    BookingService --> InvoiceDAO
    BookingService --> CustomerDAO
    CustomerService --> CustomerDAO
    AuthService --> UserDAO
    TMDBApiService --> MovieDAO

    %% UI -> Services
    POSPanel --> ShowTimeService
    POSPanel --> BookingService
    POSPanel --> CustomerService
    POSPanel --> TicketDialog
    LoginFrame --> AuthService
    MainDashboardFrame --> POSPanel

    %% Utils usage
    ConfigLoader --> TMDBApiService
    SessionManager --> LoginFrame
```

