const Loading = () => {
  return (
    <div className="loading" role="status" aria-live="polite">
      <div className="spinner" aria-hidden="true" />
      <p>Loading your storefront...</p>
    </div>
  );
};

export default Loading;
