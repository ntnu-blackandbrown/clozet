<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <footer class="footer" role="contentinfo">
    <div class="footer-content">
      <div class="footer-section">
        <h3 id="about-section">About Clozet</h3>
        <p>Your sustainable fashion marketplace. Buy, sell, and trade clothes with ease.</p>
        <div class="social-links" aria-labelledby="about-section">
          <a href="#" class="social-link" aria-label="Follow us on Instagram">Instagram</a>
          <a href="#" class="social-link" aria-label="Follow us on Facebook">Facebook</a>
          <a href="#" class="social-link" aria-label="Follow us on Twitter">Twitter</a>
        </div>
      </div>

      <div class="footer-section">
        <h3 id="navigation-section">Quick Links</h3>
        <nav aria-labelledby="navigation-section">
          <RouterLink to="/profile" class="footer-link">My Profile</RouterLink>
          <RouterLink to="/messages" class="footer-link">Messages</RouterLink>
          <RouterLink to="/create-product" class="footer-link">Sell Items</RouterLink>
        </nav>
      </div>

      <div class="footer-section">
        <h3 id="help-section">Help & Support</h3>
        <nav aria-labelledby="help-section">
          <a href="#" class="footer-link">FAQ</a>
          <a href="#" class="footer-link">Contact Us</a>
          <a href="#" class="footer-link">Terms of Service</a>
          <a href="#" class="footer-link">Privacy Policy</a>
        </nav>
      </div>

      <div class="footer-section">
        <h3 id="newsletter-section">Newsletter</h3>
        <p>Subscribe to get updates on new items and special offers!</p>
        <form class="newsletter-form" aria-labelledby="newsletter-section">
          <label for="newsletter-email" class="sr-only">Email address</label>
          <input
            type="email"
            id="newsletter-email"
            placeholder="Enter your email"
            class="form-control"
            aria-required="true"
          />
          <button type="submit" class="btn btn-primary">Subscribe</button>
        </form>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; {{ new Date().getFullYear() }} Clozet. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: var(--color-limed-spruce);
  color: var(--color-white);
  padding: var(--spacing-xl) 0 0;
  margin-top: var(--spacing-xl);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-xl);
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.footer-section h3 {
  color: var(--color-white);
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: var(--spacing-sm);
  position: relative;
  padding-bottom: var(--spacing-xs);
}

.footer-section h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background-color: var(--color-summer-green);
}

.footer-section p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  line-height: 1.5;
}

.social-links {
  display: flex;
  gap: var(--spacing-md);
}

.social-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.9rem;
  transition: var(--transition-smooth);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
}

.social-link:hover,
.social-link:focus {
  color: var(--color-white);
  background-color: rgba(255, 255, 255, 0.1);
  outline: none;
}

.social-link:focus {
  outline: 2px solid var(--color-summer-green);
  outline-offset: 2px;
}

.footer-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.9rem;
  transition: var(--transition-smooth);
  padding: var(--spacing-xs) 0;
  display: block;
}

.footer-link:hover,
.footer-link:focus {
  color: var(--color-summer-green);
  transform: translateX(5px);
  outline: none;
}

.footer-link:focus {
  outline: 2px solid var(--color-summer-green);
  outline-offset: 2px;
}

.newsletter-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.newsletter-form input {
  padding: var(--spacing-sm) var(--spacing-md);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius);
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-white);
  transition: var(--transition-smooth);
}

.newsletter-form input:focus {
  outline: none;
  border-color: var(--color-summer-green);
  background-color: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 2px var(--color-summer-green-light);
}

.newsletter-form input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.newsletter-form button {
  background-color: var(--color-summer-green);
  color: var(--color-white);
  border: none;
  border-radius: var(--border-radius);
  padding: var(--spacing-sm) var(--spacing-md);
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-bounce);
}

.newsletter-form button:hover,
.newsletter-form button:focus {
  background-color: var(--color-summer-green-dark);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
  outline: none;
}

.footer-bottom {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-md) 0;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-bottom p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.85rem;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Responsive Design */
@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-lg);
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
  }
}
</style>
