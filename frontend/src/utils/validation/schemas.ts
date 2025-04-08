import * as yup from 'yup'

// User authentication related schemas
export const usernameSchema = yup.string().required('Username is required')

export const emailSchema = yup.string().email('Invalid email').required('Email is required')

export const passwordSchema = yup
  .string()
  .required('Password is required')
  .min(8, 'Password must be at least 8 characters')
  .matches(/[A-Z]/, 'Password must contain at least one uppercase letter')
  .matches(/[a-z]/, 'Password must contain at least one lowercase letter')
  .matches(/[0-9]/, 'Password must contain at least one number')

export const confirmPasswordSchema = (passwordRef: string) =>
  yup
    .string()
    .required('Please confirm your password')
    .oneOf([yup.ref(passwordRef)], 'Passwords must match')

export const nameSchema = yup.string().required('Name is required')

// Phone validation for Norwegian format
export const norwegianPhoneSchema = yup
  .string()
  .matches(/^\+?\d{8,15}$/, 'Please enter a valid phone number')
  .required('Phone number is required')

// Norwegian postal code validation
export const norwegianPostalCodeSchema = yup
  .string()
  .matches(/^\d{4}$/, 'Please enter a valid Norwegian postal code (4 digits)')
  .required('Postal code is required')

// Composite schemas
export const loginSchema = yup.object({
  username: usernameSchema,
  password: passwordSchema,
})

export const registerSchema = yup.object({
  username: usernameSchema,
  firstName: nameSchema,
  lastName: nameSchema,
  email: emailSchema,
  password: passwordSchema,
  confirmPassword: confirmPasswordSchema('password'),
})

export const changePasswordSchema = yup.object({
  currentPassword: yup.string().required('Current password is required'),
  newPassword: passwordSchema,
  confirmPassword: confirmPasswordSchema('newPassword'),
})

export const shippingDetailsSchema = yup.object({
  firstName: nameSchema,
  lastName: nameSchema,
  streetAddress: yup.string().required('Street address is required'),
  postalCode: norwegianPostalCodeSchema,
  city: yup.string().required('City is required'),
  country: yup.string().required('Country is required'),
  phone: norwegianPhoneSchema,
})

export const vippsPaymentSchema = yup.object({
  phoneNumber: yup
    .string()
    .matches(
      /^\+47[0-9]{8}$/,
      'Please enter a valid Norwegian phone number (+47 followed by 8 digits)',
    )
    .required('Phone number is required'),
  pincode: yup
    .string()
    .matches(/^\d{4}$/, 'PIN must be 4 digits')
    .required('PIN is required'),
})
