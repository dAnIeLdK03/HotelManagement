const FooterComponent = () => {
    return (
        <footer>
            <span className="my-footer">
                 Hotel Reservation System. All rights reserved &copy; {new Date().getFullYear()};
            </span>
        </footer>
    );
};

export default FooterComponent;