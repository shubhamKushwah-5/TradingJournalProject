# Deployment Checklist

## Pre-Deployment Tasks

### Trading Journal API
- [x] All endpoints tested
- [x] JWT authentication working
- [x] Postman collection created
- [ ] application.properties updated for production
- [ ] PostgreSQL configuration added
- [ ] Environment variables prepared
- [ ] Deployment tested on Render

## Render Account Setup
- [ ] Create Render account
- [ ] Verify email
- [ ] Add payment method (free tier, but required)

## Database Setup (per API)
- [ ] Create PostgreSQL database on Render
- [ ] Get database connection string
- [ ] Update application.properties

## Deployment Steps
- [ ] Connect GitHub repository
- [ ] Configure build settings
- [ ] Set environment variables
- [ ] Deploy
- [ ] Test live endpoints
- [ ] Update README with live URL

## Post-Deployment
- [ ] Test all endpoints on live URL
- [ ] Verify database connections
- [ ] Check logs for errors
- [ ] Update resume with live URLs
- [ ] Update LinkedIn with projects