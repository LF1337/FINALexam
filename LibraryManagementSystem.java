import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = Library.loadLibraryData("libraryData.ser");
        Scanner scanner = new Scanner(System.in);

        
        library.addBook(new Book("Book 1", "Author 1", "ISBN1"));
        library.addBook(new Book("Book 2", "Author 2", "ISBN2"));
        library.addBook(new Book("Book 3", "Author 1", "ISBN3"));

        library.registerBorrower(new Borrower("Borrower 1", "borrower1@example.com"));
        library.registerBorrower(new Borrower("Borrower 2", "borrower2@example.com"));

        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Display all books");
            System.out.println("2. Find book by title");
            System.out.println("3. Find books by author");
            System.out.println("4. Borrow a book");
            System.out.println("5. Return a book");
            System.out.println("6. Save library data");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    System.out.println("All Books:");
                    library.printBooks(library.getAllBooks());
                    break;
                case 2:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    Book foundBook = library.getBookByTitle(title);
                    library.printBook(foundBook);
                    break;
                case 3:
                    System.out.print("Enter author name: ");
                    String author = scanner.nextLine();
                    List<Book> authorBooks = library.getBooksByAuthor(author);
                    library.printBooks(authorBooks);
                    break;
                case 4:
                    System.out.print("Enter your email: ");
                    String email = scanner.nextLine();
                    if (!library.isBorrower(email)) {
                        System.out.println("You are not a registered borrower!");
                        break;
                    }
                    System.out.print("Enter book title to borrow: ");
                    title = scanner.nextLine();
                    library.borrowBook(email, title);
                    break;
                case 5:
                    System.out.print("Enter your email: ");
                    email = scanner.nextLine();
                    if (!library.isBorrower(email)) {
                        System.out.println("You are not a registered borrower!");
                        break;
                    }
                    System.out.print("Enter book title to return: ");
                    title = scanner.nextLine();
                    library.returnBook(email, title);
                    break;
                case 6:
                    library.saveLibraryData("libraryData.ser");
                    System.out.println("Saved library data in libraryData.ser file");
                    break;
                case 7:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

class Library implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Book> books = new ArrayList<>();
    private List<Borrower> borrowers = new ArrayList<>();

    public void saveLibraryData(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
            System.out.println("Library data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Library loadLibraryData(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Library) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new Library();
        }
    }

    public List<Book> getAllBooks() {
        return this.books;
    }

    public void returnBook(String email, String title) {
        Borrower borrower = getBorrowerByEmail(email);
        if (borrower == null) {
            System.out.println("Invalid email");
            return;
        }
        Book bookToReturn = borrower.getBookByTitle(title);
        if (bookToReturn == null) {
            System.out.println("Invalid book title");
            return;
        }
        this.books.add(bookToReturn);
        borrower.removeBook(bookToReturn);
    }

    public void borrowBook(String email, String title) {
        Borrower borrower = getBorrowerByEmail(email);
        if (borrower == null) {
            System.out.println("Invalid email");
            return;
        }
        Book bookToBorrow = getBookByTitle(title);
        if (bookToBorrow == null) {
            System.out.println("Invalid book title");
            return;
        }
        this.books.remove(bookToBorrow);
        borrower.addBook(bookToBorrow);
    }

    public boolean isBorrower(String email) {
        return getBorrowerByEmail(email) != null;
    }

    private Borrower getBorrowerByEmail(String email) {
        for (Borrower borrower : this.borrowers) {
            if (borrower.getEmail().equalsIgnoreCase(email)) {
                return borrower;
            }
        }
        return null;
    }

    public List<Book> getBooksByAuthor(String author) {
        List<Book> authorBooks = new ArrayList<>();
        for (Book book : this.books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                authorBooks.add(book);
            }
        }
        return authorBooks;
    }

    public Book getBookByTitle(String title) {
        for (Book book : this.books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public void printBook(Book book) {
        if (book != null) {
            System.out.println(String.format("Title: %1$s, Author: %2$s, ISBN: %3$s", book.getTitle(), book.getAuthor(), book.getIsbn()));
        } else {
            System.out.println("Book not found.");
        }
    }

    public void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found!");
        } else {
            for (Book book : books) {
                System.out.println(String.format("Title: %1$s, Author: %2$s, ISBN: %3$s", book.getTitle(), book.getAuthor(), book.getIsbn()));
            }
        }
    }

    public void registerBorrower(Borrower borrower) {
        borrowers.add(borrower);
    }

    public void addBook(Book book) {
        books.add(book);
    }
}

class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }
}

class Borrower implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String email;
    private List<Book> borrowedBooks = new ArrayList<>();

    public Borrower(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void addBook(Book book) {
        borrowedBooks.add(book);
    }

    public void removeBook(Book book) {
        borrowedBooks.remove(book);
    }

    public Book getBookByTitle(String title) {
        for (Book book : borrowedBooks) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }
}
