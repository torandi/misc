shell.run("/disk/smartTurtleAPI")

local dump_position = vector.new(0, -1, 0)
local shaft_position = vector.new(0, 2, 0)

local scrap_slots = 3
local start_level = 7
local bedrock_layer = 5
local size = 5

local ssstart = (9 - scrap_slots) + 1 -- scrap slots start

smart.report("Reporting in")

function dropCrap() 
    smart.drop(9 - scrap_slots)
end

if(smart.any_free_slot(1, ssstart)) then
    print(string.format("Put something in slots %d to 9", ssstart))

    while(smart.any_free_slot(ssstart, 9)) do
        sleep(1)
    end
end

print("Going into position")

smart.goto(shaft_position, {"x", "y", "z"}, true);

print("In position.")

function find_height()
    print("Going down to bedrock, brb!")
    smart.report("Going down to bedrock, brb!")
    while(smart.down(1, true)) do end
    smart.report(string.format("Hit rock bottom at %d", smart.pos.z))
    return math.abs(smart.pos.z);
end

local top = find_height()
local home = vector.new(0, 0, top)

smart.pos.z = 0


function report(status)
    if(not smart.report(status)) then
        smart.goto(shaft_position, {"y", "x", "z"}, true)
        smart.goto(home)
        smart.axis_align("y", 1)
        smart.report("Stopped")
        error("Stopped by user")
    end
end

dump_position.z = dump_position.z + top
shaft_position.z = shaft_position.z + top

smart.goto(vector.new(0, 0, start_level), {"z"})

print("Commence mining!")

function empty_inventory()
    print("Invertory full, emptying")
    report("Invertory full, returning to surface")
    local return_to = smart.pos
    local ret_dir = smart.dir
    local shaft_pos_layer = vector.new(
        shaft_position.x,
        shaft_position.y,
        smart.pos.z
    )
    
    smart.goto(shaft_pos_layer,
        {"y", "x", "z"}, true) -- digging should not be needed, but in worst
                               -- case it should do it
    
    smart.goto(dump_position, {"z", "x", "y"}, true) --same here
    
    dropCrap()
    report("Going back down")
    
    smart.goto(shaft_pos_layer, {"y", "x", "z"})
    smart.goto(return_to, {"x", "y"}, true);
    smart.rotate_to(ret_dir)
    report("Resuming normal mining")
end

function make_space_down() 
    if(not smart.any_free_slot(1, 9)) then
        local invpos = smart.compareDown(1, 9)
        while(invpos and turtle.getItemSpace(invpos) == 0) do
            invpos = smart.compareDown(invpos + 1, 9)
        end
        if(not invpos or turtle.getItemSpace(invpos) == 0) then
            empty_inventory()
        end
    end
end

function check_ores()
    if(turtle.detectUp() and not smart.compareUp(ssstart, 9)) then
        if(not smart.any_free_slot(1, 9)) then
            local invpos = smart.compareUp(1, 9)
            while(invpos and turtle.getItemSpace(invpos) == 0) do
                invpos = smart.compareUp(invpos + 1, 9)
            end
            if(not invpos or turtle.getItemSpace(invpos) == 0) then
                empty_inventory()
            end
        end
        turtle.digUp();
    end
 
    if(turtle.detectDown() and not smart.compareDown(ssstart, 9)) then
        make_space_down();
        turtle.digDown();
    end
end

function dig_forward() 
    if(not smart.any_free_slot(1, 9)) then
        local invpos = smart.compare(1, 9)
        while(invpos and turtle.getItemSpace(invpos) == 0) do
            invpos = smart.compare(invpos + 1, 9)
        end
        if(not invpos or turtle.getItemSpace(invpos) == 0) then
            empty_inventory()
        end
    end
    smart.forward(1, true);
end

function dig_down() 
    if(not smart.any_free_slot(1, 9)) then
        local invpos = smart.compareDown(1, 9)
        while(invpos and turtle.getItemSpace(invpos) == 0) do
            invpos = smart.compareDown(invpos + 1, 9)
        end
        if(not invpos or turtle.getItemSpace(invpos) == 0) then
            empty_inventory()
        end
    end

    return smart.down(1, true);
end

function dig_line()
    check_ores()
    for i = 1, size - 1, 1 do
        dig_forward();
        check_ores();
    end
end

function dig_level()
    print(string.format("Starting on level %d", smart.pos.z))
    report(string.format("Level %d", smart.pos.z))
    for i = 1, size, 1 do
        dig_line()
        if(i ~= size) then
            if(i % 2 == 0) then
                smart.turnRight()
                dig_forward()
                smart.turnRight()
            else
                smart.turnLeft()
                dig_forward()
                smart.turnLeft()
            end
        end
    end

    smart.goto(shaft_position, {"y", "x"}, true)
    if(smart.pos.z - 3 > bedrock_layer) then
        for i = 1, 3, 1 do
            if(not dig_down()) then return false end
        end
        smart.axis_align("y", 1);
        return true
    else
        smart.axis_align("y", 1);
        return false
    end
end

while(dig_level()) do
end

report("Entering bedrock drill mode")

local drill_level = smart.pos.z;


function drill() 
    repeat
        make_space_down()
    until not dig_down()
    smart.goto(vector.new(smart.pos.x, smart.pos.y, drill_level), {"z"}, true)
end

for y = 1, size, 1 do
    report(string.format("Drilling to bedrock, y: %d", y))
    drill()
    for x = 1, size - 1, 1 do
        dig_forward()
        drill()
    end
    
    if(y ~= size) then
        if(y % 2 == 0) then
            smart.turnRight()
            dig_forward()
            smart.turnRight()
        else
            smart.turnLeft()
            dig_forward()
            smart.turnLeft()
        end
    end
end

smart.axis_align("y", 1);

print("Done mining, returing home")
report("Done mining, returing home for supper")


smart.goto(shaft_position, {"y", "x", "z"}, true)
smart.goto(home)

smart.goto(dump_position)

dropCrap()

smart.goto(home)

print("Done");
report(string.format("This is mining droid %s, signing out", smart.id))