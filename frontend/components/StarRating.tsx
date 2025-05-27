import { useState } from 'react';

interface StarRatingProps {
  maxRating?: number;
  initialRating?: number;
  onRatingChange: (rating: number) => void;
  previewRating?: number;
}

export default function StarRating({
  maxRating = 5,
  initialRating = 0,
  onRatingChange,
  previewRating = 0,
}: StarRatingProps) {
  const [rating, setRating] = useState(initialRating);
  const [hoverRating, setHoverRating] = useState(0);

  const handleStarClick = (selectedRating: number) => {
    setRating(selectedRating);
    onRatingChange(selectedRating);
  };

  const handleStarHover = (hoveredRating: number) => {
    setHoverRating(hoveredRating);
  };

  const handleStarLeave = () => {
    setHoverRating(0);
  };

  const displayRating = hoverRating > 0 ? hoverRating : (previewRating > 0 ? previewRating : rating);

  return (
    <div style={{ display: 'inline-block' }}>
      {[...Array(maxRating)].map((_, index) => {
        const starValue = index + 1;
        return (
          <span
            key={index}
            onClick={() => handleStarClick(starValue)}
            onMouseOver={() => handleStarHover(starValue)}
            onMouseLeave={handleStarLeave}
            style={{
              cursor: 'pointer',
              color: starValue <= displayRating ? '#ffc107' : '#e4e5e9',
              fontSize: '1.5rem',
              margin: '0 2px',
            }}
          >
            ★
          </span>
        );
      })}
    </div>
  );
} 