# Likes and Comments Feature

This document describes the likes and comments functionality that has been added to the Jam, Event, and Lesson pages.

## Overview

Users can now:
- **Like** jams, events, and lessons by clicking a like button
- **Unlike** by clicking the like button again (toggle functionality)
- **Post comments** on jams, events, and lessons
- **Delete their own comments** (only the comment author can delete)
- **View the total number of likes** and comments for each entity

## Database Models

### Like Models
- `Jam_Like` - Stores likes for jams
- `Event_Like` - Stores likes for events (already existed)
- `Lesson_Like` - Stores likes for lessons

Each like model contains:
- `id` - Primary key
- `user` - The user who liked the item
- `jam/event/lesson` - Reference to the liked entity
- `createdAt` - Timestamp when the like was created

### Comment Models
- `Jam_Comment` - Stores comments for jams
- `Event_Comment` - Stores comments for events
- `Lesson_Comment` - Stores comments for lessons

Each comment model contains:
- `id` - Primary key
- `user` - The user who posted the comment
- `jam/event/lesson` - Reference to the commented entity
- `content` - The comment text (TEXT field)
- `createdAt` - Timestamp when the comment was posted

## Repositories

### Like Repositories
- `JamLikeRepository`
- `EventLikeRepository`
- `LessonLikeRepository`

Key methods:
- `countByJamId/EventId/LessonId(Long id)` - Count total likes
- `existsByUserAndJam/Event/Lesson(User, Entity)` - Check if user has liked
- `deleteByUserAndJam/Event/Lesson(User, Entity)` - Remove a like
- `findByUserAndJam/Event/Lesson(User, Entity)` - Find specific like

### Comment Repositories
- `JamCommentRepository`
- `EventCommentRepository`
- `LessonCommentRepository`

Key methods:
- `findByJamIdOrderByCreatedAtDesc(Long id)` - Get all comments, newest first
- `countByJamId/EventId/LessonId(Long id)` - Count total comments

## Controller Endpoints

### Jam Controller (`/jam/{jamId}/{username}`)
- `GET /jam/{jamId}/{username}` - View jam with likes/comments
- `POST /jam/{jamId}/{username}/like` - Toggle like
- `POST /jam/{jamId}/{username}/comment` - Add comment
- `POST /jam/{jamId}/{username}/comment/{commentId}/delete` - Delete comment

### Event Controller (`/event/{eventId}/{username}`)
- `GET /event/{eventId}/{username}` - View event with likes/comments
- `POST /event/{eventId}/{username}/like` - Toggle like
- `POST /event/{eventId}/{username}/comment` - Add comment
- `POST /event/{eventId}/{username}/comment/{commentId}/delete` - Delete comment

### Lesson Controller (`/lesson/{lessonId}/{username}`)
- `GET /lesson/{lessonId}/{username}` - View lesson with likes/comments
- `POST /lesson/{lessonId}/{username}/like` - Toggle like
- `POST /lesson/{lessonId}/{username}/comment` - Add comment
- `POST /lesson/{lessonId}/{username}/comment/{commentId}/delete` - Delete comment

## Frontend Components

### Reusable Fragment
Location: `src/main/resources/templates/fragments/_likes-comments.html`

The fragment `likesComments` accepts the following parameters:
- `entityType` - Type of entity ('jam', 'event', or 'lesson')
- `entityId` - ID of the entity
- `username` - Current user's username
- `likeCount` - Total number of likes
- `userHasLiked` - Boolean indicating if current user has liked
- `comments` - List of comments

### Integration in Pages
The likes and comments section is added to:
- `jam.html` - Before the back button
- `event.html` - Before the back button
- `lesson.html` - Before the legend section

## User Interface

### Like Section
- Displays total like count as a badge
- Shows a large button that changes appearance based on user's like status:
  - **Liked**: Blue button with "❤️ Je vindt dit leuk"
  - **Not liked**: Outlined button with "🤍 Vind ik leuk"
- Clicking toggles the like status

### Comment Section
- Displays total comment count as a badge
- Comment form with textarea (required field)
- Submit button to post comment
- List of all comments ordered by newest first
- Each comment shows:
  - Username of commenter
  - Timestamp (formatted as dd-MM-yyyy HH:mm)
  - Comment content (preserves line breaks)
  - Delete button (only visible to comment author)
- Confirmation dialog before deleting a comment
- Empty state message when no comments exist

## Security & Validation

### Comment Deletion
- Users can only delete their own comments
- Ownership verification is performed server-side
- Unauthorized deletion attempts return an error page

### Comment Validation
- Comment content cannot be empty
- Whitespace-only comments are rejected
- Empty comments redirect back without error (silent failure)

### Like Validation
- Users must be authenticated to like/unlike
- Duplicate likes are prevented by database constraints
- Toggle mechanism ensures each user can only have one like per entity

## Technical Details

### Transaction Management
Comment deletion operations are annotated with `@Transactional` to ensure data consistency.

### Error Handling
All endpoints include proper error handling for:
- Entity not found (jam/event/lesson)
- User not found
- Unauthorized access attempts
- Invalid comment IDs

### Database Schema
The feature will automatically create the following tables via JPA:
- `jam_like`
- `jam_comment`
- `event_comment` (event_like already existed)
- `lesson_like`
- `lesson_comment`

Each table includes appropriate foreign key constraints to maintain referential integrity.

## Future Enhancements

Potential improvements for future development:
- Edit functionality for comments
- Nested comments/replies
- Like count on comment cards
- Real-time updates using WebSockets
- Notifications when someone likes or comments
- Pagination for comments when count is high
- Rich text formatting in comments
- Emoji reactions beyond simple likes
- User mentions (@username)
- Comment search functionality