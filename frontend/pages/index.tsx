import { useEffect, useState, useRef, useCallback } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import axios from 'axios'; // Import axios
import { API_BASE_URL } from '../config/api';
import StarRating from '../components/StarRating'; // Import StarRating component

interface User {
  firstName: string;
  lastName: string;
  email: string;
  id: string; // Assuming user ID is a string/UUID on the frontend side
}

interface Item {
  id: string; // Note: This should ideally be string/UUID to match backend
  name: string;
  description: string;
  imageUrl: string;
}

interface Rating {
  id: string;
  user: User; // Assuming the full user object might be returned, adjust if needed
  item: Item; // Assuming the full item object might be returned, adjust if needed
  rating: number | null; // Rating can be null if status is not RATED
  status: string; // e.g., RATED, DONT_CARE, DELETED, DONT_KNOW
  timestamp: string; // Assuming timestamp is returned as a string
}

const SWIPE_THRESHOLD_DONT_KNOW = 100; // Minimum horizontal distance for a 'Don't Know' swipe
const SWIPE_THRESHOLD_DONT_CARE = 200; // Minimum horizontal distance for a 'Doesn't Care' swipe (more than DONT_KNOW)
const RATING_SWIPE_RANGE = 200; // Horizontal distance range to map to 0-5 rating (for positive swipes)
const ROTATION_FACTOR = 0.1; // Adjust for desired rotation intensity
const MAX_ROTATION = 10; // Maximum rotation angle in degrees

export default function Home() {
  const router = useRouter();
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [user, setUser] = useState<User | null>(null);
  const [item, setItem] = useState<Item | null>(null);
  const [noItems, setNoItems] = useState(false);

  // State for swipe gestures
  const [startX, setStartX] = useState(0);
  const [currentX, setCurrentX] = useState(0);
  const [isSwiping, setIsSwiping] = useState(false);
  const [potentialRating, setPotentialRating] = useState(0); // State for swipe-based rating preview
  const itemContainerRef = useRef<HTMLDivElement>(null); // Ref for the item container

  // State for swipe feedback labels
  const [showLike, setShowLike] = useState(false);
  const [showNope, setShowNope] = useState(false);
  const [showDontCare, setShowDontCare] = useState(false); // State for 'Doesn't Care' label

  useEffect(() => {
    // Check if the user is logged in
    const token = localStorage.getItem('token');
    if (!token) {
      router.replace('/auth/login');
      return;
    }

    // Fetch user data
    const fetchUserData = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/api/users/current-user`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (response.ok) {
          const userData = await response.json();
          setUser(userData);
        } else {
          // If token is invalid, redirect to login
          localStorage.removeItem('token');
          router.replace('/auth/login');
        }
      } catch (error) {
        console.error('Error fetching user data:', error);
        localStorage.removeItem('token');
        router.replace('/auth/login');
      }
    };

    fetchUserData();
  }, [router]);

  // Fetch the next item for the user after user is loaded
  useEffect(() => {
    const fetchNextItem = async () => {
      if (!user) return;
      const token = localStorage.getItem('token');
      try {
        const response = await fetch(`${API_BASE_URL}/api/items/next-for-user`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const data = await response.json();
        if (data.status === 'NO_ITEMS') {
          setNoItems(true);
          setItem(null);
        } else {
          setItem(data);
          setNoItems(false);
        }
      } catch (error) {
        console.error('Error fetching next item:', error);
        setNoItems(true);
        setItem(null);
      }
    };
    fetchNextItem();
  }, [user]);

  // Handler for rating an item (called by StarRating component or swipe logic)
  const handleRateItem = useCallback(async (rating: number) => {
    if (user) {
      try {
        const token = localStorage.getItem('token');
        // Ensure item ID is treated as a string for the API call since the backend uses UUID
        const response = await axios.post<Rating>(`${API_BASE_URL}/api/items/${user.id}/rate`, rating, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        console.log(`Rated item ${user.id} with ${rating} stars. Received rating object:`, response.data);
      } catch (error) {
        console.error('Error rating item:', error);
        // Handle error (e.g., display error message)
      }
    }
  }, [user]);

  // Handler for marking an item as "Don't Know" (called by button or swipe logic)
  const handleDontKnow = useCallback(async () => {
    if (user) {
       try {
        const token = localStorage.getItem('token');
        // Ensure item ID is treated as a string for the API call since the backend uses UUID
        const response = await axios.post<Rating>(`${API_BASE_URL}/api/items/${user.id}/dont-know`, {}, {
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });
        console.log(`Marked item ${user.id} as Don't Know. Received rating object:`, response.data);
      } catch (error) {
        console.error('Error marking item as don\'t know:', error);
        // Handle error (e.g., display error message)
      }
    }
  }, [user]);

  // Handler for marking an item as "Doesn't Care"
  const handleDontCare = useCallback(async () => {
    if (user) {
       try {
        const token = localStorage.getItem('token');
        // Ensure item ID is treated as a string for the API call since the backend uses UUID
        const response = await axios.post<Rating>(`${API_BASE_URL}/api/items/${user.id}/dont-care`, {}, {
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });
        console.log(`Marked item ${user.id} as Doesn't Care. Received rating object:`, response.data);
      } catch (error) {
        console.error('Error marking item as doesn\'t care:', error);
        // Handle error (e.g., display error message)
      }
    }
  }, [user]);

  // Effect for handling swipe gestures
  useEffect(() => {
    const container = itemContainerRef.current;
    if (container) {
      const handleStart = (clientX: number) => {
        setStartX(clientX);
        setCurrentX(clientX);
        setIsSwiping(false);
        setPotentialRating(0); // Reset potential rating on start
        setShowLike(false); // Hide labels on start
        setShowNope(false); // Hide labels on start
        setShowDontCare(false); // Hide new label on start
      };

      const handleMove = (clientX: number) => {
         if (startX === 0 && currentX === 0) return; // Prevent drag without initial click/touch
        setCurrentX(clientX);
        const diffX = clientX - startX;

        if (Math.abs(diffX) > 10) { // Start swiping if moved more than 10px
             setIsSwiping(true);

             // Calculate potential rating based on swipe distance to the right
             if (diffX > 0) {
                 // Map positive swipe distance to 0-5 rating
                 const calculatedRating = Math.min(5, Math.max(0, Math.floor((diffX / RATING_SWIPE_RANGE) * 5)));
                 setPotentialRating(calculatedRating);
                 setShowLike(diffX > SWIPE_THRESHOLD_DONT_KNOW / 2); // Show Like label if swiped right enough
                 setShowNope(false); // Hide Nope label
                 setShowDontCare(false); // Hide Don't Care label

             } else {
                 // For swipe left, determine if it's Don't Know or Doesn't Care
                  setPotentialRating(0); // Reset potential rating for left swipes

                  if (diffX < -SWIPE_THRESHOLD_DONT_CARE) {
                      setShowDontCare(true); // Show Don't Care label for significant left swipe
                      setShowNope(false); // Hide Nope
                  } else if (diffX < -SWIPE_THRESHOLD_DONT_KNOW / 2) {
                      setShowNope(true); // Show Nope label for moderate left swipe
                      setShowDontCare(false); // Hide Don't Care
                  } else {
                      setShowNope(false); // Hide both if not swiped left enough
                      setShowDontCare(false);
                  }
                   setShowLike(false); // Hide Like label
             }
        } else {
             setIsSwiping(false); // Not swiping if movement is minimal
             setShowLike(false); // Hide labels if not swiping significantly
             setShowNope(false); // Hide labels if not swiping significantly
             setShowDontCare(false); // Hide labels if not swiping significantly
        }
      };

       const handleEnd = () => {
        if (isSwiping) {
          const diffX = currentX - startX;
          if (diffX > SWIPE_THRESHOLD_DONT_KNOW) {
            // Swiped right (positive rating)
            console.log('Swiped right');
            // Use the calculated potential rating, ensure it's at least 1 for a meaningful swipe right
            handleRateItem(Math.max(1, potentialRating));
          } else if (diffX < -SWIPE_THRESHOLD_DONT_CARE) {
            // Swiped left significantly (Doesn't Care)
            console.log('Swiped left - Doesn\'t Care');
            handleDontCare(); // Call the don't care handler
          } else if (diffX < -SWIPE_THRESHOLD_DONT_KNOW) {
             // Swiped left moderately (Don't Know)
            console.log('Swiped left - Don\'t Know');
            handleDontKnow(); // Call the don't know handler
          }
          // If swipe distance is within thresholds, no action is taken, and it animates back
        }
        // Reset swipe state and animate back to center
        setStartX(0);
        setCurrentX(0);
        setIsSwiping(false); // Allow transition to apply
        setPotentialRating(0); // Reset potential rating after swipe ends
        setShowLike(false); // Hide labels after swipe ends
        setShowNope(false); // Hide labels after swipe ends
        setShowDontCare(false); // Hide labels after swipe ends
      };

      const handleTouchStart = (e: TouchEvent) => handleStart(e.touches[0].clientX);
      const handleTouchMove = (e: TouchEvent) => handleMove(e.touches[0].clientX);
      const handleTouchEnd = () => handleEnd();

      const handleMouseDown = (e: MouseEvent) => {
         // Only start swipe on left mouse button
        if (e.button === 0) {
            handleStart(e.clientX);
        }
      };

       const handleMouseMove = (e: MouseEvent) => {
         if (e.button === 0) { // Only track move if left button is down
           handleMove(e.clientX);
         }
       };

       const handleMouseUp = (e: MouseEvent) => {
          if (e.button === 0) { // Only end swipe on left mouse button release
            handleEnd();
          }
       };

        const handleMouseLeave = () => {
            if(isSwiping) {
                handleEnd(); // Treat leaving the container while swiping as ending the swipe
            }
        }

      // Add event listeners
      container.addEventListener('touchstart', handleTouchStart);
      container.addEventListener('touchmove', handleTouchMove);
      container.addEventListener('touchend', handleTouchEnd);

       container.addEventListener('mousedown', handleMouseDown);
       container.addEventListener('mousemove', handleMouseMove);
       container.addEventListener('mouseup', handleMouseUp);
       container.addEventListener('mouseleave', handleMouseLeave);

      // Clean up event listeners on component unmount
      return () => {
        container.removeEventListener('touchstart', handleTouchStart);
        container.removeEventListener('touchmove', handleTouchMove);
        container.removeEventListener('touchend', handleTouchEnd);
         container.removeEventListener('mousedown', handleMouseDown);
         container.removeEventListener('mousemove', handleMouseMove);
         container.removeEventListener('mouseup', handleMouseUp);
         container.removeEventListener('mouseleave', handleMouseLeave);
      };
    }
  }, [startX, currentX, isSwiping, user?.id, handleRateItem, handleDontKnow, handleDontCare, potentialRating]); // Add handleDontCare to dependencies

  const handleLogout = () => {
    localStorage.removeItem('token');
    router.replace('/auth/login');
  };

  if (!user) {
    return (
      <div style={{
        minHeight: '100vh',
        backgroundColor: '#111827', // Dark background
        color: '#f3f4f6', // Light text
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}>
        Loading...
      </div>
    );
  }

  // Calculate swipe distance
  const swipeDistance = currentX - startX;

  // Calculate rotation based on swipe distance
  const rotation = Math.max(-MAX_ROTATION, Math.min(MAX_ROTATION, swipeDistance * ROTATION_FACTOR));

   // Determine background color based on swipe direction/distance and threshold
   const swipeBackgroundColor = swipeDistance > 0 && swipeDistance > SWIPE_THRESHOLD_DONT_KNOW / 4 ?
                              'rgba(34, 197, 94, 0.5)' : // Green for positive swipe
                              swipeDistance < 0 && Math.abs(swipeDistance) > SWIPE_THRESHOLD_DONT_CARE / 4 ?
                              'rgba(147, 51, 234, 0.5)' : // Purple for 'Doesn't Care' swipe
                              swipeDistance < 0 && Math.abs(swipeDistance) > SWIPE_THRESHOLD_DONT_KNOW / 4 ?
                              'rgba(239, 68, 68, 0.5)' : // Red for 'Don't Know' swipe
                              'transparent'; // Default

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#111827', // Dark background
      color: '#f3f4f6', // Light text
    }}>
      {/* Navigation Bar */}
      <nav style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '1rem 2rem',
        backgroundColor: '#1f2937', // Slightly lighter dark
        borderBottom: '1px solid #374151',
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        zIndex: 50
      }}>
        {/* Logo/Title */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem'
        }}>
          <span style={{
            fontSize: '1.5rem',
            fontWeight: 'bold',
            background: 'linear-gradient(90deg, #3b82f6, #60a5fa)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent'
          }}>
            HeartRate
          </span>
        </div>

        {/* Profile Dropdown */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setIsProfileOpen(!isProfileOpen)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.5rem',
              backgroundColor: 'transparent',
              border: 'none',
              color: '#f3f4f6',
              cursor: 'pointer',
              borderRadius: '0.375rem',
              transition: 'background-color 0.2s'
            }}
            onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#374151'}
            onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
          >
            <div style={{
              width: '2rem',
              height: '2rem',
              borderRadius: '50%',
              backgroundColor: '#3b82f6',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontWeight: '500'
            }}>
              {user.firstName[0]}{user.lastName[0]}
            </div>
            <span style={{ fontSize: '0.875rem' }}>
              {user.firstName} {user.lastName}
            </span>
          </button>

          {/* Dropdown Menu */}
          {isProfileOpen && (
            <div style={{
              position: 'absolute',
              right: 0,
              top: '100%',
              marginTop: '0.5rem',
              backgroundColor: '#1f2937',
              borderRadius: '0.5rem',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
              border: '1px solid #374151',
              minWidth: '12rem'
            }}>
              <button
                onClick={handleLogout}
                style={{
                  width: '100%',
                  padding: '0.75rem 1rem',
                  textAlign: 'left',
                  backgroundColor: 'transparent',
                  border: 'none',
                  color: '#f3f4f6',
                  cursor: 'pointer',
                  fontSize: '0.875rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  transition: 'background-color 0.2s'
                }}
                onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#374151'}
                onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
              >
                <span>Sign out</span>
              </button>
            </div>
          )}
        </div>
      </nav>

      {/* Main Content */}
      <main style={{
        paddingTop: '5rem',
        minHeight: 'calc(100vh - 4rem)',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '2rem'
      }}>
        {noItems ? (
          <div style={{ textAlign: 'center', maxWidth: '32rem', margin: '0 auto' }}>
            <h1 style={{ fontSize: '2rem', fontWeight: 'bold', marginBottom: '1rem', color: '#f3f4f6' }}>
              No items to rate yet!
            </h1>
            <p style={{ fontSize: '1.25rem', color: '#9ca3af', marginBottom: '2rem' }}>
              Add some items to start rating.
            </p>
            <Link href="/add-item" style={{
              display: 'inline-block',
              padding: '0.75rem 1.5rem',
              backgroundColor: '#3b82f6',
              color: 'white',
              fontWeight: 'bold',
              borderRadius: '0.375rem',
              textDecoration: 'none',
              transition: 'background-color 0.2s'
            }}
              onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#60a5fa'}
              onMouseOut={(e) => e.currentTarget.style.backgroundColor = '#3b82f6'}
            >
              Add Item
            </Link>
          </div>
        ) : item ? (
          <div style={{ textAlign: 'center', maxWidth: '32rem', margin: '0 auto' }}>
            <h1 style={{ fontSize: '2rem', fontWeight: 'bold', marginBottom: '1rem', color: '#f3f4f6' }}>
              Rate this item:
            </h1>
            <div style={{
              backgroundColor: '#1f2937',
              padding: '1.5rem',
              borderRadius: '0.5rem',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
              position: 'relative',
              zIndex: 10
            }}>
              <img
                src={`${API_BASE_URL}${item.imageUrl}`}
                alt={item.name}
                style={{
                  maxWidth: '100%',
                  height: 'auto',
                  borderRadius: '0.25rem',
                  marginBottom: '1rem'
                }}
              />
              <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '0.5rem', color: '#f3f4f6' }}>
                {item.name}
              </h2>
              <p style={{ fontSize: '1rem', color: '#9ca3af', marginBottom: '1.5rem' }}>
                {item.description}
              </p>
              {/* Rating and Interaction Buttons */}
              <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '1rem'
              }}>
                {/* Star Rating Component */}
                 {/* Pass rating from swipe for visual feedback if needed */}
                <StarRating onRatingChange={handleRateItem} previewRating={potentialRating} />

                {/* Don't Know Button */}
                <button
                  onClick={handleDontKnow}
                  style={{
                    padding: '0.75rem 1.5rem',
                    backgroundColor: '#6b7280', // Gray
                    color: 'white',
                    fontWeight: 'bold',
                    borderRadius: '0.375rem',
                    border: 'none',
                    cursor: 'pointer',
                    transition: 'background-color 0.2s',
                    width: 'fit-content'
                  }}
                  onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#4b5563'}
                  onMouseOut={(e) => e.currentTarget.style.backgroundColor = '#6b7280'}
                >
                  Don't Know
                </button>

                 {/* Doesn't Care Button */}
                 <button
                  onClick={handleDontCare}
                  style={{
                    padding: '0.75rem 1.5rem',
                    backgroundColor: '#9333ea', // Purple
                    color: 'white',
                    fontWeight: 'bold',
                    borderRadius: '0.375rem',
                    border: 'none',
                    cursor: 'pointer',
                    transition: 'background-color 0.2s',
                    width: 'fit-content'
                  }}
                  onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#7e22ce'}
                  onMouseOut={(e) => e.currentTarget.style.backgroundColor = '#9333ea'}
                >
                  Doesn't Care
                </button>
              </div>
            </div>
          </div>
        ) : null}
      </main>
    </div>
  );
} 