# auth-lib-java

Shared RBAC library for DLS-2 Spring Boot services. Reads trusted `X-User-*` headers injected by the API Gateway and enforces role-based permission checks.

## How It Works

The API Gateway validates JWTs and forwards user context as headers. This library reads those headers and provides:

1. **`@RequirePermission`** — method annotation that enforces RBAC on controller endpoints
2. **`UserContext`** — injectable controller parameter with user ID, roles, and email
3. **`Permission`** — constants for all fine-grained permissions
4. **`RolePermissions`** — role-to-permission mapping with admin wildcard

No JWT validation happens in this library — the gateway already did that.

## Usage

### Annotate controller endpoints

```java
@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    @GetMapping
    @RequirePermission(Permission.RESTAURANTS_READ)
    public List<Restaurant> getAll(UserContext user) {
        // user.userId(), user.roles(), user.email() available
        return restaurantService.findAll();
    }

    @PostMapping
    @RequirePermission(Permission.RESTAURANTS_CREATE)
    public Restaurant create(@RequestBody RestaurantRequest req, UserContext user) {
        return restaurantService.create(req);
    }
}
```

### Responses

| Status | When |
|--------|------|
| 401 | `X-User-Id` header missing (request didn't go through gateway) |
| 403 | User's roles don't grant the required permission |

### Roles and Permissions

| Role | Permissions |
|------|------------|
| `customer` | orders_create, orders_read, restaurants_read, payments_read, users_read, users_update |
| `courier` | couriers_read, couriers_update, deliveries_read, deliveries_update, restaurants_read, users_read, users_update |
| `restaurant` | restaurants_*, menu_*, users_read, users_update |
| `admin` | `*` (all permissions) |

## Auto-Configuration

The library auto-configures via Spring Boot's `AutoConfiguration.imports`. Just add the dependency — no `@EnableXxx` or manual bean registration needed.

## Build and Test

```bash
./mvnw compile
./mvnw test        # 30 tests
```
