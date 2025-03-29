<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>About Clozet</h3>
        <p>Your sustainable fashion marketplace. Buy, sell, and trade clothes with ease.</p>
        <div class="social-links">
          <a href="#" class="social-link">Instagram</a>
          <a href="#" class="social-link">Facebook</a>
          <a href="#" class="social-link">Twitter</a>
        </div>
      </div>

      <div class="footer-section">
        <h3>Quick Links</h3>
        <RouterLink to="/profile" class="footer-link">My Profile</RouterLink>
        <RouterLink to="/messages" class="footer-link">Messages</RouterLink>
        <RouterLink to="/create-product" class="footer-link">Sell Items</RouterLink>
      </div>

      <div class="footer-section">
        <h3>Help & Support</h3>
        <a href="#" class="footer-link">FAQ</a>
        <a href="#" class="footer-link">Contact Us</a>
        <a href="#" class="footer-link">Terms of Service</a>
        <a href="#" class="footer-link">Privacy Policy</a>
      </div>

      <div class="footer-section">
        <h3>Newsletter</h3>
        <p>Subscribe to get updates on new items and special offers!</p>
        <div class="newsletter-form">
          <input type="email" placeholder="Enter your email" />
          <button type="submit">Subscribe</button>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; {{ new Date().getFullYear() }} Clozet. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: #f8f9fa;
  padding: 4rem 0 0;
  margin-top: 4rem;
  border-top: 1px solid #e5e7eb;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-section h3 {
  color: #333;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.footer-section p {
  color: #6b7280;
  font-size: 0.9rem;
  line-height: 1.5;
}

.social-links {
  display: flex;
  gap: 1rem;
}

.social-link {
  color: #6b7280;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s ease;
}

.social-link:hover {
  color: #333;
}

.footer-link {
  color: #6b7280;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #333;
}

.newsletter-form {
  display: flex;
  gap: 0.5rem;
}

.newsletter-form input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
  font-size: 0.9rem;
}

.newsletter-form button {
  padding: 0.5rem 1rem;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.newsletter-form button:hover {
  background-color: #444;
}

.footer-bottom {
  margin-top: 3rem;
  padding: 1.5rem 0;
  border-top: 1px solid #e5e7eb;
  text-align: center;
}

.footer-bottom p {
  color: #6b7280;
  font-size: 0.9rem;
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .footer-content {
    grid-template-columns: 1fr;
  }

  .footer {
    padding: 3rem 0 0;
  }

  .footer-content {
    padding: 0 1rem;
  }

  .newsletter-form {
    flex-direction: column;
  }

  .newsletter-form button {
    width: 100%;
  }
}
</style>
