# Chef MGMT Frontend — Design Spec

## Overview

Angular 19 frontend for the chef management system. Uses PrimeNG (Aura theme), PrimeFlex, and PrimeIcons. No backend connection — all data is mocked via a JSON file and manipulated in-memory.

App name: `chef-mgmt-frontend`

## Package Structure

```
src/app/
├── core/
│   ├── services/          # MockDataService, ChefService, OrderService, RoleSwitcherService
│   ├── guards/            # RoleGuard
│   └── config/            # App config, routes
├── feature/
│   ├── auth/              # Role switcher component (dropdown in navbar)
│   ├── chefs/             # Chefs list, chef detail, chef form modal
│   ├── orders/            # Orders list, order form modal
│   └── dashboard/         # Dashboard with stats and recent orders
├── shared/
│   ├── components/        # Layout (navbar, footer), confirm-delete dialog
│   └── pipes/             # Date formatting, rating display
└── assets/
    └── mock-data.json     # Seed data for chefs, orders, users
```

## Technology Stack

- Angular 19 (standalone components, no NgModules)
- PrimeNG 19 with Aura theme
- PrimeFlex 4 for layout utilities
- PrimeIcons for iconography
- No custom SCSS — rely entirely on Prime styling

## Routing

| Route | Component | Access |
|-------|-----------|--------|
| `/dashboard` | DashboardComponent | ADMIN, CHEF |
| `/chefs` | ChefListComponent | ADMIN only |
| `/chefs/:id` | ChefDetailComponent | ADMIN, CHEF (chef sees own only) |
| `/orders` | OrderListComponent | ADMIN only |
| `**` | Redirect to `/dashboard` | — |

## Authentication Model

No login page. A role-switcher dropdown in the navbar allows toggling between ADMIN and CHEF views.

- `RoleSwitcherService` holds the current role and mock user as a `BehaviorSubject`
- `RoleGuard` checks the current role before route activation
- CHEF role auto-redirects to their own chef detail page and hides admin-only nav items
- Mock users: one ADMIN, one CHEF (linked to a specific chef in mock data)

## Pages and Components

### Navbar (shared)
- PrimeNG `Menubar` with logo, nav tabs (Dashboard, Chefs, Orders)
- Role switcher `Dropdown` on the right
- CHEF role hides "Chefs" and "Orders" tabs, shows only "Dashboard" and "My Profile"

### Dashboard
- Summary stat cards using PrimeNG `Card`: total chefs, total orders, average rating
- Recent orders mini-table using PrimeNG `Table` (5 most recent, read-only)

### Chefs List (ADMIN only)
- PrimeNG `Table` with:
  - Column filters: text on name/email, numeric on rating
  - Sortable column headers
  - Paginator (10 per page)
  - Checkbox selection for bulk delete
  - Inline action buttons: edit, delete, view detail
- `Toolbar` above table: "New Chef" button, "Delete Selected" button
- Delete uses `ConfirmDialog`

### Chef Detail
- Chef info displayed in a `Card`: name, email, birth date, rating (as star `Tag`)
- Edit chef button opens ChefFormModal
- Orders sub-table (same features as orders list but scoped to this chef)
- "Add Order" button opens OrderFormModal
- Inline edit/delete on each order row

### Orders List (ADMIN only)
- PrimeNG `Table` with:
  - Column filters: text on item name, numeric on price, dropdown on chef
  - Sortable column headers
  - Paginator (10 per page)
  - Checkbox selection for bulk delete
  - Inline action buttons: edit, delete
- `Toolbar` above table: "Delete Selected" button

### Chef Form Modal
- PrimeNG `Dialog` with reactive form
- Fields: name (`InputText`), email (`InputText`), birth date (`Calendar`), rating (`InputNumber`, 0-5)
- Validation matching backend constraints: name 2-30 chars, valid email, birth date 18+, rating 0-5
- Used for both create and edit (title changes accordingly)

### Order Form Modal
- PrimeNG `Dialog` with reactive form
- Fields: item name (`InputText`), total price (`InputNumber`), ordered at (`Calendar`)
- Validation: item name 2-60 chars, price > 0
- Used for both create and edit

### Confirm Delete Dialog
- PrimeNG `ConfirmDialog` via `ConfirmationService`
- Used for single delete and bulk delete operations

## Data Layer

### Mock Data (assets/mock-data.json)
Contains arrays of chefs, orders, and users matching the backend DTOs:
- ~10 chefs with varied ratings
- ~30 orders distributed across chefs
- 2 users: one ADMIN, one CHEF (linked to a chef by chefId)

### Services

**MockDataService**
- Loads JSON on app init
- Holds chefs, orders, users in-memory arrays
- All mutations happen in-memory (lost on refresh — acceptable for mock)

**ChefService**
- `getAll(filter)` — filtering, sorting, pagination over in-memory data
- `getById(id)` — single chef with orders
- `create(chef)` — generates UUID, adds to array
- `update(id, chef)` — updates in place
- `delete(id)` — removes chef and their orders
- `deleteBulk(ids)` — removes multiple chefs

**OrderService**
- `getAll(filter)` — filtering, sorting, pagination
- `getByChefId(chefId, filter)` — orders for a specific chef
- `create(chefId, order)` — generates UUID, links to chef
- `update(chefId, orderId, order)` — updates in place
- `delete(chefId, orderId)` — removes order
- `deleteBulk(ids)` — removes multiple orders

**RoleSwitcherService**
- `currentRole$: BehaviorSubject<UserRole>` — emits ADMIN or CHEF
- `currentUser$: BehaviorSubject<MockUser>` — emits current mock user data
- `switchRole(role)` — updates both subjects

### Guards

**RoleGuard**
- Functional guard using `canActivate`
- Checks `RoleSwitcherService.currentRole$`
- Routes declare required roles in `data` property
- Unauthorized → redirect to `/dashboard`

## Design Decisions

- **Standalone components** — Angular 19 best practice, no NgModules
- **PrimeNG lazy-load pattern on tables** — even though data is local, services return `CollectionResponse`-shaped results matching the backend DTO, making the switch to real API seamless later
- **No HttpInterceptor** — not needed until backend connection
- **No custom SCSS** — all styling via PrimeFlex utilities and PrimeNG component props
- **In-memory mutations** — data resets on page refresh, acceptable for mock stage
