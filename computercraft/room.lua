local args = { ... }

local width = 18
local height = 22

local roof_height = 0
local room_height = 2

if(#args == 2) then
    width = args[0]
    height = args[1]
end


pos = vector.new(0, 0, 0);

dir = vector.new(0, 1, 0);

--[[
function eq(v1, v2) then
    return (math.floor(v1.x) == math.floor(v2.x)) and (math.floor(v1.y) == math.floor(v2.y)) and (math.floor(v1.z) == math.floor(v2.z))
end

]]--


function forward() 
    if(turtle.forward()) then
        pos = pos + dir;
        return true;
    else
        return false;
    end
end

function back() 
    if(turtle.back()) then
        pos = pos - dir;
        return true;
    else
        return false;
    end
end

function up()
    if(turtle.up()) then
        pos.z = pos.z + 1;
        return true;
    else
        return false;
    end
end


function down()
    if(turtle.down()) then
        pos.z = pos.z + 1;
        return true;
    else
        return false;
    end
end

function turnLeft()
    if(dir.x == 0) then
        if(dir.y == 1) then
            dir = vector.new(-1, 0)
        else
            dir = vector.new(1 ,0)
        end
    elseif(dir.x == 1) then
        dir = vector.new(0, 1)
    else -- x == -1
        dir = vector.new(0 ,-1)
    end
    turtle.turnLeft();
end

function turnRight()
    if(dir.x == 0) then
        if(dir.y == 1) then
            dir = vector.new(1, 0)
        else
            dir = vector.new(-1 ,0)
        end
    elseif(dir.x == 1) then
        dir = vector.new(0, -1)
    else -- x == -1
        dir = vector.new(0 ,1)
    end
    turtle.turnRight();
end

function check_right()
    turtle.turnRight();
    local detect = turtle.detect();
    turtle.turnLeft();
    return detect;
end


function check_left()
    turtle.turnLeft();
    local detect = turtle.detect();
    turtle.turnRight();
    return detect;
end

function force_dig_forward()
    turtle.dig();
    while(not forward()) do
        turtle.dig();
    end
end

function dig_room(width, height, room_height)
    local start_pos = pos;
    print(string.format("Build room: %d x %d x %d.", width, height, room_height));
    -- go down 
    for i = 1, roof_height, 1 do
        turtle.digDown()
        down();
    end
    
    for z = 1, room_height, 1 do
        turtle.digDown()
        down();
        for x = 1, width, 1 do
            local y = 0
            while(y < height) do
                turtle.dig()
                if(forward()) then
                    y = y + 1
                end
            end
            
            if(x ~= width) then
                if(x % 2 == 0) then
                    turnRight();
                else
                    turnLeft();
                end
                force_dig_forward();
                if(x % 2 == 0) then
                    turnRight();
                else
                    turnLeft();
                end
            end
        end
        
        if ( (width % 2) ~= 0) then
            for i = 1, height, 1 do
                back();
            end
            turnRight();
        else
            turnLeft();
        end
    
        for i = 1, width, 1 do
            force_dig_forward();
        end
        
        turnLeft();
    end
    
    for i = 0, room_height + roof_height, 1 do
        up();
    end
    
    print("Room done! Enjoy :D");
end

dig_room(width, height, room_height);