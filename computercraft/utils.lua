if(not __utils_included) then
    __utils_included = true
    
    -- Compares v1 and v2 on given axes (default {"x", "y", "z" })
    function veq(v1, v2, axes)
        if(not axes) then axes = {"x", "y", "z"} end
        for _, axis in ipairs(axes) do 
            if(math.floor(v1[axis]) ~= math.floor(v2[axis])) then
                return false;
            end
        end
        return true
    end
    
    function vcopy(vec)
        return vector.new(vec.x, vec.y, vec.z);
    end
    
    -- returns -1 or 1. num == 0 returns 1
    function sgn(num)
        if(num >= 0) then
            return 1
        else
            return -1
        end
    end
end