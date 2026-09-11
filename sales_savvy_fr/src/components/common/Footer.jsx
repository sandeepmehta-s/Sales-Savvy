import React from 'react';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer text-light py-4 mt-auto">
      <div className="container d-flex flex-column flex-md-row justify-content-between gap-2">
        <p className="mb-0">
          &copy; {currentYear} <strong>SalesSavvy</strong>
        </p>
        <p className="mb-0 text-white-50">Thoughtful products. Simple checkout.</p>
      </div>
    </footer>
  );
};

export default Footer;
