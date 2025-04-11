

## Category
- id  - unique, pk, 
- name - unique, not null
- is_active - default true
## equipment
- id - unique, pk
- code - unique, not null
- name - not null
- status - not null, default 0
    - Enum: kiểm tra từ [0, 4]
        - 0: "Không sử dụng"
        - 1: "Đang sử dụng"
        - 2: "Đang bảo trì"
        - 3: "Đã hỏng"
        - 4: "Đã thanh lý"
- category - fk, not null
- image - fk, nullable
- regular_maintenance_day - int > 0, not null
- last_maintenance_time - default created_at
- description - nullable
- is_active - default true, not null
- created_at - default now
## equipment_maintenance
- id - pk, unique
- equipment_id - fk, not null
- maintenance_id - fk, not null
- technician_id - fk, not null
- description - nullable
- result - enum, not null
    - Enum: kiểm tra từ [0, 3]
        - 0: "Bình thường"
        - 1: "Cần sửa chữa"
        - 2: "Hỏng"
        - 3: "Cần thanh lý"
- repair_name - nullable
- repair_price - decimal, default 0
- inspection_date - nullable
- created_at - default now
## Image
- id - unique, pk
- filename - not null
- created_date - not null, default now
- path - not null

## Maintenance
- id - not null, pk
- title - not null
- description - nullable
- start_datetime - not null, default created_date
- end_datetime - not null
- created_date - not null, default now

## Maintenance_repair_suggestion
-
## user


### Viết tắt, giải thích
- unique : duy nhất
- 